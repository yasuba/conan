package com.itv.conan

import java.nio.ByteBuffer
import java.nio.file.Files

import com.itv.conan.CreateLambdaFunction._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import sbt.Keys._
import sbt.{Def, _}

import scala.sys.process.Process

object ConanPlugin extends AutoPlugin {

  object autoImport extends LambdaKeys
  import autoImport._

  override def trigger = allRequirements
  override def requires: ScalaJSPlugin.type = ScalaJSPlugin

  override def projectSettings: Seq[Def.Setting[_]] = {
    Seq[Def.Setting[_]](
      scalaJSUseMainModuleInitializer := true,
      jsDependencies ++= Seq(
        ProvidedJS / "lambda-exports.js"
      ),
      packageJSDependencies / skip := false,
      zipJs := {
        val jsFile: sbt.Attributed[sbt.File] = (Compile / fullOptJS).value
        val depsFile = (Compile / packageJSDependencies).value
        val zipFile = target.value / "lambda.zip"
        val inputs: Seq[(File, String)] = Seq((depsFile, "index.js")) ++ (Seq(
          jsFile.data) pair Path.flat)
        IO.zip(inputs, zipFile)
        zipFile
      },
      packageJsonPath := packageJsonPath
        .??(undefinedKeyError(packageJsonPath.key))
        .value,
      targetDirectory := targetDirectory
        .??(undefinedKeyError(targetDirectory.key))
        .value,
      zipNode := {
        val zipFile = zipJs.value
        streams.value.log.info("Running npm install")
        Process(Seq("cp", packageJsonPath.value, targetDirectory.value)).!
        Process("/usr/local/bin/npm install",
                new java.io.File(targetDirectory.value)).!
        Process("zip -r ../lambda.zip node_modules/aws-sdk",
                new File(targetDirectory.value)).!
        zipFile
      },
      resolvedQaConanConfig := qaConanConfig
        .??(undefinedKeyError(qaConanConfig.key))
        .value,
      resolvedPrdConanConfig := prdConanConfig
        .??(undefinedKeyError(prdConanConfig.key))
        .value,
      infradevDeploy := {
        val config = resolvedQaConanConfig.value
        import config._
        val client = lambdaClientBuilder(region)
        val result = createOrUpdateLambdaAndEventSource(
          client,
          functionName,
          functionHandler,
          zipFunctionCode.value,
          lambdaRoleArn,
          eventSourceArn
        )
        streams.value.log.info(s"Uploaded handler to QA: $result")
      },
      infraprdDeploy := {
        val config = resolvedPrdConanConfig.value
        import config._
        val client = lambdaClientBuilder(region)
        val result = createOrUpdateLambdaAndEventSource(
          client,
          functionName,
          functionHandler,
          zipFunctionCode.value,
          lambdaRoleArn,
          eventSourceArn
        )
        streams.value.log.info(s"Uploaded handler to PRD: $result")
      }
    )
  }

  def zipFunctionCode: Def.Initialize[Task[ByteBuffer]] = Def.task {
    val zipFile: java.io.File = (Compile / zipNode).value
    val bytes = Files.readAllBytes(zipFile.toPath)
    ByteBuffer.wrap(bytes)
  }

  def undefinedKeyError[A](key: AttributeKey[A]): A = {
    sys.error(
      s"${key.description.getOrElse("A required key")} is not defined. " +
        s"Please declare a value for the `${key.label}` key."
    )
  }
}

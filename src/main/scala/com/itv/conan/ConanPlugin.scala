package com.itv.conan

import java.nio.ByteBuffer
import java.nio.file.Files

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import com.itv.conan.CreateLambdaFunction._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import sbt.Keys._
import sbt._
import complete.DefaultParsers._

object ConanPlugin extends AutoPlugin {

  object autoImport extends LambdaKeys
  import autoImport._

  override def trigger = allRequirements
  override def requires: ScalaJSPlugin.type = ScalaJSPlugin

  def undefinedKeyError[A](key: AttributeKey[A]): A = {
    sys.error(
      s"${key.description.getOrElse("A required key")} is not defined. " +
        s"Please declare a value for the `${key.label}` key."
    )
  }

  override def projectSettings: Seq[Def.Setting[_]] = {
    Seq[Def.Setting[_]](
      scalaJSUseMainModuleInitializer := true,
      jsDependencies += ProvidedJS / "lambda-exports.js",
      packageJSDependencies / skip := false,
      Global / scalaJSStage := FullOptStage,
      zipJs := {
        val jsFile: sbt.Attributed[sbt.File] = (Compile / fullOptJS).value
        val depsFile = (Compile / packageJSDependencies).value
        val tf = target.value
        val zipFile = tf / "lambda.zip"
        val inputs: Seq[(File, String)] = Seq((depsFile, "index.js")) ++ (Seq(
          jsFile.data) pair Path.flat)

        IO.zip(inputs, zipFile)
        zipFile
      },
      conanConfig := {
        conanConfig.??(undefinedKeyError(conanConfig.key)).value
      },
      lambdaClient := {
        println("conf is " + conanConfig.value)
        //      val region = conanConfig.value
        AWSLambdaClientBuilder
          .standard()
          .withCredentials(DefaultAWSCredentialsProviderChain.getInstance)
          .withRegion("")
          .build()
      },
      uploadFunction := {
        //      val lambdaConfig = conanConfig.value
        //      import lambdaConfig._
        val zipFile: java.io.File = (Compile / zipJs).value
        val bytes = Files.readAllBytes(zipFile.toPath)
        val byteBuffer = ByteBuffer.wrap(bytes)

        val result = {
          createOrUpdateNewFunction(
            lambdaClient.value,
            "",
            "",
            "",
            byteBuffer,
            ""
          )
          createOrUpdateEventSourceMapping(
            lambdaClient.value,
            "",
            ""
          )
        }

        streams.value.log.info(s"uploaded handler : $result")
      }
    )
  }
}

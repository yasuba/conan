package com.itv.conan

import sbt._

trait LambdaKeys {
  lazy val packageJsonPath = settingKey[String]("Path to the package.json")
  lazy val targetDirectory = settingKey[String](
    "Path to the target directory for compiled scalajs and node modules")
  lazy val zipJs =
    taskKey[File]("Build zip file containing all generated JS and exporter")
  lazy val zipNode =
    taskKey[File]("Zips node modules up with scalajs code")
  lazy val qaConanConfig =
    settingKey[PublishLambdaConfig](
      "Config required to publish lambda to qa AWS")
  lazy val prdConanConfig =
    settingKey[PublishLambdaConfig](
      "Config required to publish lambda to prd AWS")
  lazy val resolvedQaConanConfig =
    settingKey[PublishLambdaConfig]("QA Config resolved from user input")
  lazy val resolvedPrdConanConfig =
    settingKey[PublishLambdaConfig]("PRD Config resolved from user input")
  lazy val infradevDeploy = taskKey[Unit]("Deploy lambda to infradev")
  lazy val infraprdDeploy = taskKey[Unit]("Deploy lambda to infraprd")
  def publishLambdaConfigBuilder: PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder()
}

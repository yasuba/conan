package com.itv.conan

import com.amazonaws.services.lambda.AWSLambda
import sbt._

trait LambdaKeys {
  lazy val lambdaClient = taskKey[AWSLambda]("builds an AWSLambda")
  lazy val zipJs = taskKey[File]("build zip file containing all generated JS and exporter")
  lazy val uploadFunction = inputKey[Unit]("upload lambda function")
  lazy val conanConfig = settingKey[String]("Config required to publish lambda to AWS")
  def publishLambdaConfigBuilder: PublishLambdaConfig.type = PublishLambdaConfig
}

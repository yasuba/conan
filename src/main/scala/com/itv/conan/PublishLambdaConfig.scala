package com.itv.conan


case class PublishLambdaConfig(functionName: String,
                               functionHandler: String,
                               region: String,
                               eventSourceArn: String,
                               lambdaRoleArn: String) {

  def withName(name: String): PublishLambdaConfig =
    this.copy(functionName = name)

  def withFunctionHandler(hnadler: String): PublishLambdaConfig =
    this.copy(functionHandler = hnadler)

  def withRegion(region: String): PublishLambdaConfig =
    this.copy(region = region)

  def withEventSourceArn(eventSource: String): PublishLambdaConfig =
    this.copy(eventSourceArn = eventSource)

  def withLambdaRoleArn(role: String): PublishLambdaConfig =
    this.copy(lambdaRoleArn = role)
}

object PublishLambdaConfig {
  def apply(): PublishLambdaConfig = {
   println("cfreating config")

    PublishLambdaConfig("", "", "", "", "")
  }
}

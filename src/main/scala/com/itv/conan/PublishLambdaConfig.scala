package com.itv.conan

case class PublishLambdaConfig(functionName: String,
                               functionHandler: String,
                               region: String,
                               eventSourceArn: String,
                               lambdaRoleArn: String)

object PublishLambdaConfigBuilder {
  implicit def getPublishLambdaConfig(
      builder: PublishLambdaConfigBuilder): PublishLambdaConfig =
    PublishLambdaConfig(
      builder.functionName,
      builder.functionHandler,
      builder.region,
      builder.eventSourceArn,
      builder.lambdaRoleArn
    )

  def apply(): PublishLambdaConfigBuilder = {
    PublishLambdaConfigBuilder("", "", "", "", "")
  }
}

case class PublishLambdaConfigBuilder(functionName: String,
                                      functionHandler: String,
                                      region: String,
                                      eventSourceArn: String,
                                      lambdaRoleArn: String) {

  def withName(name: String): PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder(name,
                               functionHandler,
                               region,
                               eventSourceArn,
                               lambdaRoleArn)

  def withFunctionHandler(handler: String): PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder(functionName,
                               handler,
                               region,
                               eventSourceArn,
                               lambdaRoleArn)

  def withRegion(region: String): PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder(functionName,
                               functionHandler,
                               region,
                               eventSourceArn,
                               lambdaRoleArn)

  def withEventSourceArn(eventSource: String): PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder(functionName,
                               functionHandler,
                               region,
                               eventSource,
                               lambdaRoleArn)

  def withLambdaRoleArn(role: String): PublishLambdaConfigBuilder =
    PublishLambdaConfigBuilder(functionName,
                               functionHandler,
                               region,
                               eventSourceArn,
                               role)
}

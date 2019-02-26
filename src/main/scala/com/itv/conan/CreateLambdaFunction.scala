package com.itv.conan

import java.nio.ByteBuffer
import java.util

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.lambda.model._
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClientBuilder}
import com.amazonaws.{AmazonWebServiceResult, ResponseMetadata}

import scala.util.{Failure, Success, Try}

object CreateLambdaFunction {

  def maybeLambdaConfig(lambdaClient: AWSLambda,
                        functionName: String): Try[FunctionConfiguration] =
    Try {
      lambdaClient
        .getFunction(new GetFunctionRequest().withFunctionName(functionName))
        .getConfiguration
    }

  def maybeEventSourceMapping(
      eventSourceArn: String,
      lambdaClient: AWSLambda): Try[ListEventSourceMappingsResult] =
    Try {
      lambdaClient
        .listEventSourceMappings(
          new ListEventSourceMappingsRequest()
            .withEventSourceArn(eventSourceArn)
        )
    }

  def createEventSourceMappingResult(
      lambdaClient: AWSLambda,
      functionName: String,
      eventSourceArn: String): CreateEventSourceMappingResult =
    lambdaClient.createEventSourceMapping(
      new CreateEventSourceMappingRequest()
        .withFunctionName(functionName)
        .withEventSourceArn(eventSourceArn)
    )

  def createOrUpdateEventSourceMapping(
      lambdaClient: AWSLambda,
      functionName: String,
      eventSourceArn: String): AmazonWebServiceResult[ResponseMetadata] =
    maybeEventSourceMapping(eventSourceArn, lambdaClient) match {
      case Failure(e) =>
        println(
          s"There was a problem fetching the eventSourceMapping: ${e.getMessage}")
        createEventSourceMappingResult(lambdaClient,
                                       functionName,
                                       eventSourceArn)

      case Success(mapping) =>
        val mappings: util.List[EventSourceMappingConfiguration] =
          mapping.getEventSourceMappings
        if (mappings.isEmpty)
          createEventSourceMappingResult(lambdaClient,
                                         functionName,
                                         eventSourceArn)
        else
          lambdaClient.updateEventSourceMapping(
            new UpdateEventSourceMappingRequest()
              .withUUID(mapping.getEventSourceMappings.get(0).getUUID)
              .withFunctionName(functionName))
    }

  def createOrUpdateNewFunction(
      lambdaClient: AWSLambda,
      functionName: String,
      handlerName: String,
      byteBuffer: ByteBuffer,
      lambdaExecRole: String): AmazonWebServiceResult[ResponseMetadata] =
    maybeLambdaConfig(lambdaClient, functionName) match {
      case Failure(e) =>
        println(
          s"I couldn't find a function named $functionName, I will try to create one: ${e.getMessage}")
        lambdaClient
          .createFunction(
            new CreateFunctionRequest()
              .withFunctionName(functionName)
              .withHandler(handlerName)
              .withCode(new FunctionCode().withZipFile(byteBuffer))
              .withRole(lambdaExecRole)
              .withRuntime(Runtime.Nodejs810)
          )
          .withVpcConfig(
            new VpcConfigResponse()
              .withSecurityGroupIds(
                new DescribeSecurityGroupsRequest().getGroupIds)
              .withSubnetIds(new DescribeSubnetsRequest().getSubnetIds))

      case Success(_) =>
        lambdaClient
          .updateFunctionCode(
            new UpdateFunctionCodeRequest()
              .withFunctionName(functionName)
              .withZipFile(byteBuffer)
          )
    }

  def lambdaClientBuilder(region: String): AWSLambda =
    AWSLambdaClientBuilder
      .standard()
      .withCredentials(DefaultAWSCredentialsProviderChain.getInstance)
      .withRegion(region)
      .build()

  def createOrUpdateLambdaAndEventSource(lambdaClient: AWSLambda,
                                         functionName: String,
                                         functionHandler: String,
                                         byteBuffer: ByteBuffer,
                                         lambdaRoleArn: String,
                                         eventSourceArn: String) = {
    createOrUpdateNewFunction(
      lambdaClient,
      functionName,
      functionHandler,
      byteBuffer,
      lambdaRoleArn
    )

    createOrUpdateEventSourceMapping(
      lambdaClient,
      functionName,
      eventSourceArn
    )
  }
}

import sbt._

object Dependencies {

  val awsLambda = "com.amazonaws" % "aws-java-sdk-lambda" % "1.11.500"
  val awsEc2    = "com.amazonaws" % "aws-java-sdk-ec2"    % "1.11.500"

  val all = Seq(awsLambda, awsEc2)
}
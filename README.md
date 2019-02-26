# Conan the Deployer

This plugin allows ITV ScalaJS lambdas to be deployed to AWS.

To use it:

1. Have to have a `lambda-exports.js` file in resources folder which 
   calls main scala function like this:
   ```
   const <moduleName> = require("<functionName>-opt.js");
   
   exports.handler = function(event, context) {
       <moduleName>.<MainObject>.<functionName>(event, context);
   }
   ```
   
2. Put `addSbtPlugin("com.itv" % "conan" % "0.1")` to your `plugins.sbt`

3. And `enablePlugins(ConanPlugin)` in your `build.sbt`

4. In project settings in `build.sbt` you need `qaConanConfig` and `prdConanConfig` taskKeys. These should contain: 
    ```
    publishLambdaConfigBuilder
        .withName(<funtionName>)
        .withFunctionHandler(<handlerName>)
        .withRegion(<AWS region>)
        .withEventSourceArn(eventArn)
        .withLambdaRoleArn(roleArn)
    ``` 
    
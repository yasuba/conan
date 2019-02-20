# Conan the Deployer

1. Have to have a `lambda-exports.js` file in resources folder which 
   calls main scala function like this:
   ```
   require("<functionName>-opt.js");
   
   exports.handler = function(event, context) {
       <FunctionMain>.<functionName>(event, context);
   }
   
2. Put `addSbtPlugin("com.itv" % "conan" % "0.1-SNAPSHOT")` to your `plugins.sbt`

3. And `enablePlugins(ConanPlugin)` in your `build.sbt`
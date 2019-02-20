lazy val root = (project in file("."))
  .settings(
      organization := "com.itv",
      name := "conan",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.12.8",
      libraryDependencies ++= Dependencies.all,
      sbtPlugin := true,
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
  )

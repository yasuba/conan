lazy val root = (project in file("."))
  .settings(
      name := "conan",
      version := "0.1",
      scalaVersion := "2.12.8",
      libraryDependencies ++= Dependencies.all,
      sbtPlugin := true,
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
  )

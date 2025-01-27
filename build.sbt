val tapirVersion = "1.11.13"

lazy val rootProject = (project in file("."))
  .enablePlugins(JavaAgent, JavaAppPackaging)
  .settings(
  Seq(
    javaAgents += "io.opentelemetry.javaagent" % "opentelemetry-javaagent" % "2.11.0",
    name := "tapirtest",
    version := "0.1.0-SNAPSHOT",
    organization := "example",
    scalaVersion := "2.13.16",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-json-upickle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
    )
  )
)
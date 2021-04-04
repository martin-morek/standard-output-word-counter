import sbt.Keys.libraryDependencies

name := "ziverge"

version := "0.1"

scalaVersion := "2.13.5"

val AkkaVersion = "2.6.13"
val AkkaHttpVersion = "10.2.4"
val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "org.scalactic" %% "scalactic" % "3.2.5",
  "org.scalatest" %% "scalatest" % "3.2.5" % Test
)
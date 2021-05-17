val scalaV = "2.13.6"
val specs2V = "4.11.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.14",
  "com.typesafe.akka" %% "akka-stream" % "2.6.14",
  "org.specs2" %% "specs2-core" % specs2V % "test"
)

scalaVersion := scalaV
scalacOptions ++= Seq("-deprecation", "-feature")

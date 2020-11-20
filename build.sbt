val scalaV = "2.13.4"
val specs2V = "4.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.27",
  "org.specs2" %% "specs2-core" % specs2V % "test"
)

scalaVersion := scalaV
scalacOptions ++= Seq("-deprecation", "-feature")

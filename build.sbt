val scalaV = "2.13.7"
val specs2V = "4.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.14",
  "com.typesafe.akka" %% "akka-stream" % "2.6.15",
  "org.specs2" %% "specs2-core" % specs2V % "test"
)

scalaVersion := scalaV
scalacOptions ++= Seq("-deprecation", "-feature")

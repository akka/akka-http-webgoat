val scalaV = "2.12.7"
val specs2V = "4.3.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-stream" % "2.5.18",
  "org.specs2" %% "specs2-core" % specs2V % "test"
)

scalaVersion := scalaV

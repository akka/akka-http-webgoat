scalaVersion := "2.13.11"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.15",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
)

scalaVersion := "2.13.11"
scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.15",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
)

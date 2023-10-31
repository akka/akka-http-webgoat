crossScalaVersions := Seq("2.13.12", "3.3.0")
scalaVersion := crossScalaVersions.value.head
scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
)

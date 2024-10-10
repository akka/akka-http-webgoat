crossScalaVersions := Seq("3.5.1", "2.13.15")
scalaVersion := crossScalaVersions.value.head
scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
)

val scalaV = "2.12.7"
val specs2V = "4.3.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-stream" % "2.5.18",
  "org.specs2" %% "specs2-core" % specs2V % "test"
)

scalaVersion := scalaV

//Fortify plugin
val buildid = "akkagoat"
val sca_version= "19.1"
val plugin_version = "1.0.12"

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += "lightbend-commercial-releases" at "https://repo.lightbend.com/commercial-releases/"
scalacOptions += s"-P:fortify:build=${buildid}"
scalacOptions += s"-P:fortify:scaversion=${sca_version}"
addCompilerPlugin("com.lightbend" %% "scala-fortify" % plugin_version classifier "assembly" cross CrossVersion.patch)
val translate: TaskKey[Unit] = taskKey("Fortify Translation")
translate := Def.sequential(
  clean in Compile,
  compile in Compile
).value

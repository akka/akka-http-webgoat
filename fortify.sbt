// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.25"
    cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=23.1",
  "-P:fortify:build=akka-http-webgoat"
)

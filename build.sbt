name := "pekko-streams"

version := "0.1"

scalaVersion := "3.3.3"

lazy val pekkoVersion = "1.0.2"
lazy val scalaTestVersion = "3.2.18"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor" % pekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
  "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion,
  "org.apache.pekko" %% "pekko-testkit" % pekkoVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion
)


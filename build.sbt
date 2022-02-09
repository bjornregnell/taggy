ThisBuild / scalaVersion := "3.1.3-RC1-bin-20220205-8e2fab7-NIGHTLY"
ThisBuild / version := "0.0.1"
scalacOptions ++= Seq("-language:experimental.fewerBraces")
Compile / console / initialCommands  := """
  import language.experimental.fewerBraces
  import taggy.*
"""
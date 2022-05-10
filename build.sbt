// see latest nightly version here:
// https://repo1.maven.org/maven2/org/scala-lang/scala3-compiler_3/
ThisBuild / scalaVersion := "3.2.0-RC1-bin-20220502-0e83a80-NIGHTLY"
ThisBuild / version := "0.0.1"
scalacOptions ++= Seq("-language:experimental.fewerBraces")
Compile / console / initialCommands  := """
  import language.experimental.fewerBraces
  import taggy.*
"""
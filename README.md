# taggy
* A simple-to-use zero-dependency [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html) library for tagging structured text to be unparsed as md, html or latex.

# How to use

* Use `taggy` with `sbt`: 
```
val taggyVer = "0.0.1"
libraryDependencies += "taggy" % "taggy" % taggyVer from 
  s"https://github.com/bjornregnell/taggy/releases/download/v$taggyVer/taggy_3-$tabbyVer.jar"
```

# How to build

`sbt package`

# How to publish

`sbt package` and upload jar to releases of this repo
# taggy

* A simple-to-use zero-dependency [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html) library for tagging structured text to be unparsed as md, html or latex.

* You need to have `latexmk` from [TexLive](https://tug.org/texlive/) if you want to generate pdf.

# How to use

* use `taggy` with [`scala-cli`](https://scala-cli.virtuslab.org/)
```
//> using lib "taggy:taggy:0.0.1,url=https://github.com/bjornregnell/taggy/releases/download/v0.0.1/taggy_3-0.0.1.jar"
//> using scala "3.nightly"

//  run this command in terminal to create slides in target/out.pdf 
//  scala-cli run .

import scala.language.experimental.fewerBraces
import taggy.*

@main def run = slides.toPdf()

def slides = document("Taggy Slide Example"):
  frame("Greetings in two languages"):
    p("Nice greetings:")
    itemize:
      p("English: Hello world!")
      p("Swedish: Hej världen!")
      p("https://cs.lth.se/edaa65/")
```

* Use `taggy` with [`sbt`](https://www.scala-sbt.org/) 
```
val taggyVer = "0.0.1"
libraryDependencies += "taggy" % "taggy" % taggyVer from 
  s"https://github.com/bjornregnell/taggy/releases/download/v$taggyVer/taggy_3-$tabbyVer.jar"
```

* Minimal slide example:


# How to build

`sbt package`

# How to publish

`sbt package` and upload jar to releases of this repo
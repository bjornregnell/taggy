# What is `taggy`?


* `taggy` provides an easy-to-use api for **tagging structured text**, see example below.

* With `taggy` you can make **good-looking slides** in pdf via latex. Future plans include generation of markdown and html.

* `taggy` is a zero-dependency [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html) library that shows how you can construct a concise and type-safe Scala-embedded DSL using contextual abstraction in Scala 3. 

* WARNING: `taggy` is a proof-of-concept and still under development. It benefits from an experimental feature of the Scala 3.2.nightly compiler called [`fewerBraces`](https://docs.scala-lang.org/sips/fewer-braces.html).

* You need to have `latexmk` from [TexLive](https://tug.org/texlive/) on your path.

# How to use `taggy`?

* Example usage of `taggy` with [`scala-cli`](https://scala-cli.virtuslab.org/) that automatically downloads `taggy` as a Github dependency
```
// Two magic comments used by scala-cli (just copy-paste them):
//> using lib "taggy:taggy:0.0.2,url=https://github.com/bjornregnell/taggy/releases/download/v0.0.2/taggy_3-0.0.2.jar"
//> using scala "3.2.nightly"

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
    p("https://github.com/bjornregnell/taggy")
```

See bigger example [here](https://github.com/bjornregnell/taggy/tree/main/example.scala).

# How to build `taggy`?

`scala-cli package taggy.scala -o taggy_3-0.0.2.jar --library`

# How to publish `taggy`? 

* Bump all versions to `0.0.n` in README.md and example.scala by replacing all `0.0.{n - 1}` by `0.0.n`  
* Draft a new release on https://github.com/bjornregnell/taggy/releases
* Build and upload jar named `taggy_3-0.0.n.jar` 
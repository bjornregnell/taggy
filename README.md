# What is `taggy`?


* `taggy` is an easy-to-use library for **tagging structured text**, see [example below](https://github.com/bjornregnell/taggy#how-to-use-taggy).

* With `taggy` you can make **good-looking slides** in pdf via latex. See example [here](https://github.com/bjornregnell/taggy/releases/download/v0.0.5/out.pdf). Future plans include generation of markdown and html. Contributions welcome.

* `taggy` is a zero-dependency [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html) micro-library that shows how you can construct a concise and type-safe embedded DSL using a modern Scala abstraction mechanism called [**Contextual Abstraction**](https://docs.scala-lang.org/scala3/reference/contextual/index.html) 

* `taggy` is pleasant to use thanks to the simple syntax of [**Optional Braces**](https://docs.scala-lang.org/scala3/reference/other-new-features/indentation.html).

* You need to have `latexmk` from [TexLive](https://tug.org/texlive/) on your path to build slides in pdf. Install it on Ubuntu using `sudo apt install texlive-full`

# How to use `taggy`?

* See example usage of `taggy` below using the new [`scala` runner](https://www.scala-lang.org/blog/2024/08/22/scala-3.5.0-released.html) that can automatically download `taggy` as a Github dependency. 

* Copy-paste the text below and save it in a file called `my-slides.scala` or similar in an empty new folder and do `scala-cli run .` in that folder.
```scala
//> using scala 3.5.0
//> using dep "taggy:taggy:1.0.0,url=https://github.com/bjornregnell/taggy/releases/download/v1.0.0/taggy_3-1.0.0.jar"

// Above 2 "magic" comments: just copy-paste them, bump Scala version if Latest is newer.
// Latest Scala release here: https://www.scala-lang.org/

// run this command with at least Scala 3.5 in terminal to create slides in target/out.pdf 
// scala run .

import scala.language.experimental.fewerBraces
import taggy.*

@main def run = slides.toPdf()

def slides = document("Taggy Slide Example", author = "Oddput Clementine"):
  frame("Greetings in two languages"):
    p("Nice greetings:")
    itemize:
      p("English: Hello world!")
      p("Swedish: Hej världen!")
    p("By micro-lib https://github.com/bjornregnell/taggy and awesome Scala")
```

* Instead of doing the copy-paste-save above you can just run this command in Ubuntu and you should get to see slides in your pdf viewer when ready:
```bash
curl -fL https://github.com/bjornregnell/taggy/releases/download/v1.0.0/init-slides.sh | bash
```

* See more examples in [example.scala](https://github.com/bjornregnell/taggy/tree/main/example.scala).


NOTE: If you run into errors like `Latex ERROR in target/out.log` and `Package babel Error: Unknown option 'swedish'.` when latexmk is compiling then you are missing a latex package and need to do `sudo apt install texlive-lang-european` (if you do `sudo apt install texlive-full` you will get everything needed but its a bigger install). More info [here](https://users.scala-lang.org/t/first-stable-release-of-taggy-for-easy-slides-showcasing-contextual-abstraction/10170/5)


# How to build `taggy`?

Using Scala >= 3.5.0 create a library jar with this command:
```bash
scala package taggy.scala -o taggy_3-1.0.0.jar --library
```

# How to publish `taggy`? 

Publishing requires maintainer access. If you use `gh` see manual: https://cli.github.com/manual/

* Bump all versions in README.md, example.scala, init-slides.sh
* Test everything:
      
      scala clean .
      rm -rf target
      scala run .
      xdg-open target/out.pdf
* Commit and push all changes
      
      git commit -am "prepare release"
      git push
* Draft a new release on https://github.com/bjornregnell/taggy/releases 
      
      gh release create v1.0.1 --generate-notes
* Build (see above) and upload jar named `taggy_3-1.0.1.jar` and `init-slides.sh`
      
      gh release upload v1.0.1 taggy_3-1.0.1.jar init-slides.sh target/out.pdf

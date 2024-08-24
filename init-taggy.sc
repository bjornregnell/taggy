//> using scala 3.5.0
//> using dep "taggy:taggy:1.0.0,url=https://github.com/bjornregnell/taggy/releases/download/v1.0.0/taggy_3-1.0.0.jar"

// Above 2 "magic" comments: just copy-paste them, bump Scala version if Latest is newer.
// Latest Scala release here: https://www.scala-lang.org/

// run this command with at least Scala 3.5 in terminal to create slides in target/out.pdf
// scala run .

import taggy.*

def slides = document("Taggy Slide Example", author = "Oddput Clementine"):
  frame("Greetings in two languages"):
    p("Nice greetings:")
    itemize:
      p("English: Hello world!")
      p("Swedish: Hej världen!")
    p("By micro-lib https://github.com/bjornregnell/taggy and awesome Scala")

def main =
  println("Compiling and running using scala")
  slides.toPdf()
  println("Now you can open the pdf-file in target")

main

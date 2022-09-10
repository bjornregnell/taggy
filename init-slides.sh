#!/bin/bash -v
cat > slides.scala<< EOF
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
EOF
scala-cli run .
xdg-open target/out.pdf

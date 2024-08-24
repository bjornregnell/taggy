#!/bin/bash

DEST=slides.scala

if [ -f "$DEST" ]; then
  echo "File $DEST already exists. Exit and delete it if you want to re-generate."
  read -p "Enter another file name (or just enter to exit): " DEST
  if test -z "$DEST"; then
    echo "No file given. Exiting without creating anything."
    exit 0
  fi
  echo "Creating $DEST"
fi

cat > "$DEST" << EOF
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
EOF

echo "Compiling and running $DEST using scala-cli"
scala-cli run "$DEST"

echo "Opening pdf-file in target"
xdg-open target/out.pdf

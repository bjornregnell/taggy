#!/bin/bash

DEST=slides.scala

if [ -f "$DEST" ]; then
  echo "file $DEST already exists"
  read -p "Enter another file name (or just enter to exit): " DEST
  if test -z "$DEST"; then
    echo "No file given. Exiting without creating anything."
    exit 0
  fi
  echo "Creating $DEST"
fi

cat > "$DEST" << EOF
// Two magic comments used by scala-cli (just copy-paste them):
//> using dep "taggy:taggy:0.0.5,url=https://github.com/bjornregnell/taggy/releases/download/v0.0.5/taggy_3-0.0.5.jar"
//> using scala "3.4.1"

//  run this command in terminal to create slides in target/out.pdf 
//  scala-cli run .

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

echo "Compiling and running $DEST using scala-cli"
scala-cli run "$DEST"

echo "Opening pdf-file in target"
xdg-open target/out.pdf

import taggy.* 

@main def test = 
  val doc: Tree = 
    document:
      title"Hello"
      h1"The Gurka Story"
      t"hejsan"
      t"sve*js*an"
      p" på dejsan"
      slide("fin slajd"):
        t"hejsan"
      numbered:
        p"item 1"
        t"item 2"
        p"item 3"
        items:
          p"subitem 1"

  println(doc)
  println(doc.show)
  println(doc.toLatex)
  
import taggy.*
@main def test = 
  val doc: Tree = 
    document:
      h1"The Gurka Story"
      t"hejsan"
        t"svejsan"
      p" på dejsan"
      items:
        p"item 1"
        t"item 2"
        p"item 2"

  println(doc)
  println(doc.show)
  println(doc.toLatex)
  
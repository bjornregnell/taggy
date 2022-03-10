import taggy.*
@main def test = 
  val doc: Tree = 
    document:
      h1"The Gurka Story"
      t"hejsan"
        t"svejsan"
      items:
        p"item 1"

  println(doc)
  println(doc.show)
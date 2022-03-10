import taggy.*
@main def test = 
  val doc: Tree = 
    document:
      title("The Gurka Story")
      t"hejsan"
      t"svejsan"
      items:
        p"item 1"

  println(doc)
  println(doc.show)
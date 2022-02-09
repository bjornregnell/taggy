import taggy.*
@main def test = 
  val doc = root("doc","§title=hej"){
    t"hejsan"
    t"svejsan"
    appendBranch("items"){
      t"item 1"
    }
  }
  println(doc)
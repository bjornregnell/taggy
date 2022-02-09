import taggy.*
@main def test = 
  val doc = root("doc","·title·hej·"){
    t"hejsan"
    t"svejsan"
    appendBranch("items","$itemtype=bullets"){
      t"item 1"
    }
  }
  println(doc)
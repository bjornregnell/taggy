import taggy.* 

@main def test = 
  val doc: Tree = 
    document:
      slide("Vad är öppen källkod?"):
        p"Källkod som"
        numbers:
          p"är fritt tillgänglig"
          p"får modifieras"
          p"får distribueras"
        
        p"Oftast sker utvecklingen"
        bullets: 
          p"i samverkande gemenskap" 
          p"enligt medföljande licens"
  println(doc)
  println(doc.show)
  println(doc.toLatex)
  val exitValue = Latex.mk(doc)
  println(ls())
  if exitValue != 0 then 
    println(load("out.log"))
    //throw Exception("Error during latexmk")

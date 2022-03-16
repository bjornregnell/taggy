package taggy
import Tag.*

extension (tree: Tree)
  def toLatex: String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => Latex.env("document",tail)
        case Slide     => Latex.env("Slide",tail) //TODO title in t.value
        case Title     => Latex.cmd("title", t.value) ++ "\n" ++ tail
        case Heading1  => Latex.cmd("section", t.value) ++ "\n" ++ tail
        case Text      => s"${t.value}$tail"
        case Paragraph => s"${t.value}$tail\n"
        case Items | Numbered => 
          val body = t.sub.map(
            _ match 
              case Tree(tag, value, sub*) if tag == Paragraph || tag == Text => 
                s"\\item $value\n" ++ sub.map(loop).mkString
              case st if st.tag == Items => loop(st) 
              case st => throw Exception(s"illegal tag inside ${t.tag}: ${st.tag}")
          )
          val env = if t.tag == Items then "itemize" else "enumerate"
          Latex.env(env, body.mkString)
    loop(tree)


object Latex:
  def cmd(command: String, body: String): String = s"\\$command{$body}"

  def env(environment: String, body: String): String = 
    val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
    s"\n\\begin{$environment}\n$body$newlineAfterBody\\end{$environment}"

  def mk(tree: Tree, out: String = "generated", workingDir: String = "."): Unit = 
    val latexMkTest = scala.sys.process.Process(Seq("latexmk", "-version"))
    if latexMkTest.! != 0 then throw Exception("latexmk missing on path")
    val tex = tree.toLatex
    tex.save(s"$workingDir/$out.tex")
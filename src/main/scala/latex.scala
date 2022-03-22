package taggy
import Tag.*

extension (tree: Tree)
  def toLatex: String = Latex.fromTree(tree)
  def mkLatex(outFileNoSuffix: String = "out", workDir: String = "."): Unit = 
    Latex.mk(tree, outFileNoSuffix: String, workDir: String)

object Latex:
  def fromTree(tree: Tree): String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => Latex.env("document")(tail)
        case Slide     => Latex.envArg("Slide")(t.value)(tail)
        case Title     => Latex.cmdArg("title")(t.value) ++ "\n" ++ tail
        case Heading1  => Latex.cmdArg("section")(t.value) ++ "\n" ++ tail
        case Text      => s"${t.value}$tail"
        case Paragraph => s"${t.value}$tail\n"
        case Items | Numbered => 
          val body = t.sub.map(
            _ match 
              case Tree(tag, value, sub*) if tag == Paragraph || tag == Text => 
                  s"\\item $value\n" ++ sub.map(loop).mkString
              case st if st.tag == Items || st.tag == Numbered => loop(st) 
              case st => throw Exception(s"illegal tag inside ${t.tag}: ${st.tag}")
          )
          val listEnv = if t.tag == Items then "itemize" else "enumerate"
          Latex.env(listEnv)(body.mkString)
    loop(tree)

  def brackets(params: String*): String = 
    if params.isEmpty then "" else params.mkString("[",",","]")

  def braces(params: String*): String = 
    if params.isEmpty then "" else params.mkString("{",",","}")

  def cmd(command: String): String = s"\\$command"

  def cmdArg(command: String)(args: String*): String = 
      s"\\$command${braces(args*)}"

  def cmdOptArg(command: String)(opts: String*)(args: String*): String = 
    s"\\$command${brackets(opts*)}${braces(args*)}"

  def env(environment: String)(body: String): String = 
    val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
    s"\n\\begin{$environment}\n$body$newlineAfterBody\\end{$environment}"
  
  def envArg(environment: String)(args: String*)(body: String): String = 
    val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
    s"\n\\begin{$environment}${braces(args*)}\n$body$newlineAfterBody\\end{$environment}"

  def mk(tree: Tree, out: String = "out", workDir: String = ".")(using LatexPreamble): Int = 
    import scala.sys.process.{Process  => OSProc}
    createDirIfNotExist(workDir)
    (summon[LatexPreamble].value ++ tree.toLatex).save(s"$workDir/$out.tex")
    val wd = java.io.File(workDir)
    val proc = OSProc(Seq("latexmk", "-pdf", "-cd", "-halt-on-error", "-silent", s"$out.tex"), wd)
    val procOutputFile = java.io.File(s"$workDir/$out.log")
    proc.#>(procOutputFile).run.exitValue

 
case class LatexPreamble(value: String)
object LatexPreamble:
  given defaultLatexPreamble: LatexPreamble = simpleSlides

  def simpleSlides = LatexPreamble(s"""
    \\documentclass{beamer}
    
    \\beamertemplatenavigationsymbolsempty
    \\setbeamertemplate{footline}[frame number] 
    \\setbeamercolor{page number in head/foot}{fg=gray} 
    \\usepackage[swedish]{babel}
    
    \\usepackage[utf8]{inputenc}
    \\usepackage[T1]{fontenc}
    \\usepackage[scaled=0.95]{beramono} % inconsolata or beramono ???
    \\usepackage[scale=0.9]{tgheros}
    \\newenvironment{Slide}[2][]
      {\\begin{frame}[fragile,environment=Slide,#1]{#2}}
      {\\end{frame}} 
    """.minMargin
  ) 
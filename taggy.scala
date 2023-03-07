package taggy

def loadLines(path: String): Seq[String] =
  io.Source.fromFile(path, "UTF-8").getLines.toSeq 

def selectFrom(path: String)(fromUntil: (String, String)): String = 
  val (from, until) = (fromUntil(0).trim, fromUntil(1).trim)
  val xs = loadLines(path).dropWhile(! _.trim.startsWith(from))
  (xs.take(1) ++ xs.drop(1).takeWhile(x => 
    if until.isEmpty then x.trim.nonEmpty 
    else ! x.trim.startsWith(until)
  )).mkString("\n")

def createDirs(path: String): Boolean = 
  java.io.File(path).mkdirs()

extension (s: String) def saveTo(path: String): Unit =
    val pw = java.io.PrintWriter(java.io.File(path), "UTF-8")
    try pw.write(s) finally pw.close()

enum Tag:
  case Document, Frame, Itemize, Enumerate, Paragraph, Code, Space, Image

export Tag.*

class Tree(var tag: Tag, var value: String): 
  val sub = collection.mutable.Buffer[Tree]() 

extension (t: Tree)
  def show: String = 
    def loop(t: Tree, level: Int): String = 
      val indent = "  " * level
      val node = s"""${t.tag}${if t.value.isEmpty then ":" else "(" + t.value + ")"}""" 
      val subnodes = t.sub.map(st => loop(st, level + 1)).mkString("","","")
      s"$indent$node\n$subnodes"
    loop(t, 0)

  def toLatex: String = Latex.fromTree(t)

  def toPdf(out: String = "out", dir: String = "target")(using Preamble): Unit = 
    Latex.make(t, out, dir)

type TreeContext = Tree ?=> Unit

def root(tag: Tag, value: String)(body: TreeContext): Tree =
  given t: Tree = Tree(tag, value)
  body
  t

def leaf(tag: Tag, value: String): TreeContext = 
  summon[Tree].sub += Tree(tag, value)

def branch(tag: Tag, value: String = "")(body: TreeContext): TreeContext = 
  val subTree = Tree(tag, value)
  body(using subTree)
  summon[Tree].sub += subTree

def document(title: String)(body: TreeContext): Tree = root(Document, title)(body)
def itemize(body: TreeContext): TreeContext = branch(Itemize)(body)
def enumerate(body: TreeContext): TreeContext = branch(Enumerate)(body)
def frame(title: String)(body: TreeContext): TreeContext = branch(Frame, title)(body)
def p(text: String): TreeContext = leaf(Paragraph, text)
def code(text: String): TreeContext = leaf(Code, text)
def codeFromUntil(file: String)(fromUntil: (String, String)): TreeContext = 
  code(selectFrom(file)(fromUntil))
def space(lines: Double = 1.0): TreeContext = leaf(Space, lines.toString)
def image(file: String): TreeContext = leaf(Image, file)

object Latex:
  def fromTree(tree: Tree): String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => env("document")(s"\\title{${t.value}}\\maketitle\n$tail")
        case Frame     => envArg("frame")("fragile")(t.value.replaceAllMarkers)(tail)
        case Paragraph => s"${t.value.replaceAllMarkers}$tail\n"
        case Itemize | Enumerate => 
          val body = t.sub.map(
            _ match 
              case st if st.tag == Paragraph => 
                  s"\\item ${st.value.replaceAllMarkers}\n" ++ st.sub.map(loop).mkString
              case st if st.tag == Itemize || st.tag == Enumerate => loop(st) 
              case st => throw Exception(s"illegal tag inside ${t.tag}: ${st.tag}")
          )
          val listEnv = if t.tag == Itemize then "itemize" else "enumerate"
          env(listEnv)(body.mkString)
        case Code => env("Scala")(t.value.minimizeMargin)
        case Space => cmdArg("vspace")(s"${t.value}em")
        case Image => 
          cmd("vfill") ++ 
            env("center")(cmdOptArg("includegraphics")("width=0.2\\textwidth")(t.value))
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
    s"\n\\begin{$environment}\n$body\n\\end{$environment}\n\n"

  def envArg(environment: String)(bracketArgs: String*)(braceArgs: String*)(body: String): String = 
    s"\n\\begin{$environment}${brackets(bracketArgs*)}${braces(braceArgs*)}\n$body\n\\end{$environment}\n\n"

  def beginEndPattern(s: String) = s"$s([^$s]*)$s".r

  def urlPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".r

  val replacePatterns = Vector[(util.matching.Regex, (String, String, Int))]( 
    beginEndPattern("\\*\\*") -> ("\\\\textbf{", "}", 1),
    beginEndPattern("\\*")    -> ("\\\\textit{", "}", 1),
    beginEndPattern("\\`")    -> ("\\\\lstinline[language=]{", "}", 1),
    urlPattern                -> ("{\\\\footnotesize{\\\\url{", "}}}", 0),
  )

  extension (s: String) 
    def replaceAllMarkers: String = 
      var result = s
      for (pattern, (b, e, i)) <- replacePatterns do
        result = pattern.replaceAllIn(result, m => s"$b${m.group(i)}$e")
      result

    def minimizeMargin: String = 
      val xs = s.split("\n")
      val minMargin = xs.filter(_.nonEmpty).map(_.takeWhile(_.isWhitespace).length).minOption.getOrElse(0)
      xs.map(_.drop(minMargin)).mkString("\n")
  end extension
 
  def make(tree: Tree, out: String, workDir: String)(using pre: Preamble): Int = 
    import scala.sys.process.{Process  => OSProc}               // rename import
    createDirs(workDir)
    (pre.value ++ tree.toLatex).saveTo(s"$workDir/$out.tex")
    val wd = java.io.File(workDir)
      val proc = OSProc(Seq("latexmk", "-pdf", "-cd", "-halt-on-error", "-silent", s"$out.tex"), wd)
      val procOutputFile = java.io.File(s"$workDir/$out.log")
      val result = proc.#>(procOutputFile).run.exitValue
      if result == 0 then println(s"Latex output generated in $workDir/$out.pdf")
      else println(s"Latex ERROR in $workDir/$out.log")
      result


case class Preamble(value: String)
object Preamble:
  given defaultPreamble: Preamble = Preamble(slideTemplate())

  def slideTemplate(): String = s"""
\\documentclass{beamer}
\\beamertemplatenavigationsymbolsempty
\\setbeamertemplate{footline}[frame number] 
\\setbeamercolor{page number in head/foot}{fg=gray} 
\\usepackage[swedish]{babel}
\\usepackage[utf8]{inputenc}
\\usepackage[T1]{fontenc}
\\usepackage{tgheros, beramono}
\\usepackage{fancyvrb}
\\usepackage{xcolor}
\\definecolor{mygreen}{rgb}{0,0.4,0}
\\definecolor{mylinkcolor}{rgb}{0,0.1,0.5}
\\definecolor{myemphcolor}{rgb}{0,0.4,0.1}
\\definecolor{myalertcolor}{rgb}{0.4,0.1,0}
\\definecolor{eclipsepurple}{rgb}{0.5,0,0.25}
\\definecolor{eclipseblue}{rgb}{0.16,0,1.0}
\\definecolor{eclipsegreen}{rgb}{0,0.5,0}
\\usepackage{listings}

%%% lingstings specifics:

\\lstdefinelanguage{Scala}{
  morekeywords={abstract,case,catch,class,def,%
    do,else,enum,export,extends,false,final,finally,%
    for,given,if,implicit,import,lazy,match,%
    new,null,object,override,package,%
    private,protected,return,sealed,%
    super,then,throw,trait,true,try,%
    type,val,var,while,with,yield,%
    as, derives, end, extension, infix, inline, opaque, open, transparent, using}, % soft keywords
  otherkeywords={=>,<-,<:,>:,@,=>>,?=>},
  sensitive=true,
  morecomment=[l]{//},
  morecomment=[n]{/*}{*/},
  morestring=[b]",
  morestring=[b]',
  morestring=[b]\"\"\"
}

\\lstset{
    language=Scala,
    tabsize=2,
    basicstyle=\\ttfamily,
    keywordstyle=\\bfseries\\color{eclipsepurple},
    commentstyle=\\color{mygreen},
    numberstyle={\\footnotesize},
    numbers=none,
    %backgroundcolor=\\color{gray!15},
    frame=none, %single,
    rulecolor=\\color{black!25},
    %title={\\footnotesize\\lstname},
    breaklines=false,
    breakatwhitespace=false,
    framextopmargin=2pt,
    framexbottommargin=2pt,
    showstringspaces=false,
    columns=fullflexible,keepspaces
}

\\lstset{literate=%
{Å}{{\\AA}}1
{Ä}{{\\"A}}1
{Ö}{{\\"O}}1
{Ü}{{\\"U}}1
{ß}{{\\ss}}1
{ü}{{\\"u}}1
{å}{{\\aa}}1
{ä}{{\\"a}}1
{ö}{{\\"o}}1
{æ}{{\\ae}}1
{ø}{{\\o}}1
{Æ}{{\\AE}}1
{Ø}{{\\O}}1
{`}{{\\`{}}}1
{─}{{\\textemdash}}1
{└}{{|}}1
{├}{{|}}1
{│}{{|}}1
{♠}{{$$\\spadesuit$$}}1
{♥}{{$$\\heartsuit$$}}1
{♣}{{$$\\clubsuit$$}}1
{♦}{{$$\\diamondsuit$$}}1
}

\\lstnewenvironment{Scala}[1][]{%
    \\lstset{#1}%
}{}  
  """ 

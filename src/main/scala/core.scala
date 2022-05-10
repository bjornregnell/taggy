package taggy

def loadLines(path: String): Seq[String] =               // top-level definition
  io.Source.fromFile(path, "UTF-8").getLines.toSeq 

def selectFrom(path: String)(fromUntil: (String, String)): String = 
  val (from, until) = (fromUntil(0).trim, fromUntil(1).trim)  // apply on tuples
  val xs = loadLines(path).dropWhile(! _.trim.startsWith(from))
  (xs.take(1) ++ xs.drop(1).takeWhile(x => 
    if until.isEmpty then x.trim.nonEmpty                  // new control syntax
    else ! x.trim.startsWith(until)
  )).mkString("\n")

def createDirs(path: String): Boolean = 
  java.io.File(path).mkdirs()         // universal apply methods: new not needed

extension (s: String) def saveTo(path: String): Unit =      // extension methods
    val pw = java.io.PrintWriter(java.io.File(path), "UTF-8")
    try pw.write(s) finally pw.close()

enum Tag:          // scalable enums, from simple to generic algebraic datatypes
  case Document, Frame, Itemize, Enumerate, Paragraph, Code

export Tag.* // tailor the namespace and api with export, no need for forwarders

class Tree(var tag: Tag, var value: String): 
  val sub = collection.mutable.Buffer[Tree]() 

extension (t: Tree)                              // collective extension methods
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

                  //end markers are checked by the compiler

type TreeContext = Tree ?=> Unit              // abstract over context functions

def root(tag: Tag, value: String)(body: TreeContext): Tree =   //builder-pattern
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
def codeFrom(file: String)(fromUntil: (String, String)): TreeContext = 
  code(selectFrom(file)(fromUntil))



package taggy

case class Tree(tag: String, value: String, sub: Tree*)

class TreeBuilder(val tag: String, val value: String, initSub: TreeBuilder*):
  val sub: scala.collection.mutable.Buffer[TreeBuilder] = initSub.toBuffer 
  def toTree: Tree = Tree(tag, value, sub.map(_.toTree).toSeq*)

type Builder = TreeBuilder ?=> Unit

def root(tag: String, value: String = "")(body: Builder): Tree = 
  given tb: TreeBuilder = TreeBuilder(tag, value)
  body
  tb.toTree

def appendLeaf(tag: String)(value: String): Builder = 
  summon[TreeBuilder].sub += TreeBuilder(tag, value)

def appendBranch(tag: String, value: String = "")(body: Builder): Builder = 
  val subTree = TreeBuilder(tag, value)
  body(using subTree)
  summon[TreeBuilder].sub += subTree

def appendStringContext(tag: String, sc: StringContext, args: Any*): Builder = 
  val strings = sc.parts.iterator
  val expressions = args.iterator
  // interpolate strings with expressions:
  var buf = new StringBuilder(strings.next())
  while strings.hasNext do
    buf.append(expressions.next())
    buf.append(strings.next())
  // append lines as leafs to parent
  val lines = buf.toString.split("\n").iterator
  while lines.hasNext do
    val line = lines.next()
    appendLeaf(tag)(line)

extension (sc: StringContext) 
  def t(args: Any*): Builder = appendStringContext("text", sc, args*)
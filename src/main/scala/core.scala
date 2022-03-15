
package taggy

case class Tree(tag: Tag, value: String, sub: Tree*):
  def show: String = 
    def loop(t: Tree, level: Int): String = 
      val indent = "  " * level
      val node = s"""${t.tag}${if t.value.isEmpty then ":" else "(" + t.value + ")"}""" 
      val subnodes = t.sub.map(st => loop(st, level + 1)).mkString("","","")
      s"$indent$node\n$subnodes"
    loop(this, 0)

class TreeBuilder(val tag: Tag, val value: String, initSub: TreeBuilder*):
  val sub: scala.collection.mutable.Buffer[TreeBuilder] = initSub.toBuffer 
  def toTree: Tree = Tree(tag, value, sub.map(_.toTree).toSeq*)

type TreeContext = TreeBuilder ?=> Unit

def root(tag: Tag, value: String = "")(body: TreeContext): Tree = 
  given tb: TreeBuilder = TreeBuilder(tag, value)
  body
  tb.toTree

def appendLeaf(tag: Tag)(value: String): TreeContext = 
  summon[TreeBuilder].sub += TreeBuilder(tag, value)

def appendBranch(tag: Tag, value: String = "")(body: TreeContext): TreeContext = 
  val subTree = TreeBuilder(tag, value)
  body(using subTree)
  summon[TreeBuilder].sub += subTree

def appendStringContext(tag: Tag, sc: StringContext, args: Any*): TreeContext = 
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



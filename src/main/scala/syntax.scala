package taggy

object Tags:
  val Document = "document"
  val Items    = "items"
  val Title    = "title"
  val Text     = "text"
  val Para     = "para"
  val H1       = "h1"

import Tags.*

def document(body: TreeContext): Tree = root(Document)(body)
def items(body: TreeContext): TreeContext = appendBranch(Items)(body)
def title(s: String): TreeContext = appendLeaf(Title)(s)

extension (sc: StringContext) 
  def t(args: Any*): TreeContext = appendStringContext(Text, sc, args*)
  def p(args: Any*): TreeContext = appendStringContext(Para, sc, args*)
  def h1(args: Any*): TreeContext = appendStringContext(H1, sc, args*)
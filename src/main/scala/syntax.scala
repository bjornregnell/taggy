package taggy

def document(body: TreeContext): Tree = root("document")(body)
def items(body: TreeContext): TreeContext = appendBranch("items")(body)
def title(s: String): TreeContext = appendLeaf("title")(s)

extension (sc: StringContext) 
  def t(args: Any*): TreeContext = appendStringContext("text", sc, args*)
  def p(args: Any*): TreeContext = appendStringContext("paragraph", sc, args*)
  def h1(args: Any*): TreeContext = appendStringContext("h1", sc, args*)
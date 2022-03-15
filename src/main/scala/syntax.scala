package taggy

enum Tag:
  case Document, Slide, Items, Numbered, Title, Text, Paragraph, Heading1

import Tag.*

def document(body: TreeContext): Tree = root(Document)(body)
def items(body: TreeContext): TreeContext = appendBranch(Items)(body)
def numbered(body: TreeContext): TreeContext = appendBranch(Numbered)(body)
def slide(title: String)(body: TreeContext): TreeContext = appendBranch(Slide, title)(body)
  
extension (sc: StringContext) 
  def title(args: Any*): TreeContext = appendStringContext(Title, sc, args*)
  def t(args: Any*): TreeContext = appendStringContext(Text, sc, args*)
  def p(args: Any*): TreeContext = appendStringContext(Paragraph, sc, args*)
  def h1(args: Any*): TreeContext = appendStringContext(Heading1, sc, args*)
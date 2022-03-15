package taggy
import Tag.*

def latexCmd(cmd: String, body: String): String = s"\\$cmd{$body}"

def latexEnv(environment: String, body: String): String = 
  val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
  s"\n\\begin{$environment}\n$body$newlineAfterBody\\end{$environment}"

extension (tree: Tree)
  def toLatex: String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => latexEnv("document",tail)
        case Slide     => latexEnv("Slide",tail) //TODO title in t.value
        case Title     => latexCmd("title", t.value) ++ "\n" ++ tail
        case Heading1  => latexCmd("section", t.value) ++ "\n" ++ tail
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
          latexEnv(env, body.mkString)
    loop(tree)
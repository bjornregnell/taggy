package taggy


def latexCmd(cmd: String, body: String): String = s"\\$cmd{$body}"

def latexEnv(environment: String, body: String): String = 
  val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
  s"\n\\begin{$environment}\n$body$newlineAfterBody\\end{$environment}"

extension (t: Tree)
  def toLatex: String =
    def loop(t: Tree): String =
      import Tags.*
      def sub = t.sub.map(loop).mkString
      t.tag match
        case Document  => latexEnv("document",sub)
        case H1        => latexCmd("title",t.value) ++ "\n" ++ sub
        case Text      => s"${t.value}$sub"
        case Para      => s"${t.value}$sub\n"
        case Items     => 
          val body = t.sub.map{st => 
            (st match 
              case Tree(tag, value, sub*) if tag == Para|| tag == Text => 
                s"\\item $value\n" ++ sub.map(loop).mkString
              
              case _ => st.sub.map(loop).mkString 
          )}
          latexEnv("itemize", body.mkString)
        case t           => throw Exception(s"unknown tag: $t")
    loop(t)
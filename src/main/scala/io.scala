package taggy


def ls(dir: String = "."): Vector[String] =
  Option(java.io.File(dir).list).map(_.toVector).getOrElse(Vector())

def load(fileName: String): String =
  var result: String = ""
  val source = scala.io.Source.fromFile(fileName, "UTF-8")
  try result = source.mkString finally source.close()
  result

extension (s: String)
  def save(fileName: String): Unit = 
    val f = java.io.File(fileName)
    val pw = java.io.PrintWriter(f, "UTF-8")
    try pw.write(s) finally pw.close()
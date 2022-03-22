package taggy

extension (s: String)
  def nbrOfInitSpaces: Int = s.indexWhere(! _.isSpaceChar) // -1 if empty
  def minMargin: String = 
    val xs = s.split("\n")
    val min: Int = xs.map(_.nbrOfInitSpaces).filter(_ > 0).minOption.getOrElse(0)
    xs.map(_.drop(min)).mkString("\n")

  def isInit(init: String): Boolean = s.dropWhile(_.isSpaceChar).startsWith(init) 
  def stripInit(init: String): String = s.dropWhile(_.isSpaceChar).stripPrefix(init)

  def parse: TreeBuilder =
    def parseInit(init: String, tag: Tag, lines: Seq[String], tb: TreeBuilder): Boolean = 
      val foundInit = lines(0).isInit(init)
      if foundInit then 
        tb.sub += TreeBuilder(tag, lines(0).stripInit(init))
        val tail = lines.tail
        val nextHeading: Int = tail.indexWhere(_.isInit(init))
        val (subLines, nextHeadingLines) = tail.splitAt(nextHeading)
        if subLines.nonEmpty then loop(subLines, tb.sub.last)
        if nextHeadingLines.nonEmpty then 
          if nextHeading >= 0 then loop(nextHeadingLines, tb)
          else loop(nextHeadingLines, tb.sub.last)
      foundInit
    end parseInit

    def loop(lines: Seq[String], tb: TreeBuilder): Unit = 
      if lines.nonEmpty then 
        if      parseInit("# ",  Tag.Heading1, lines, tb) then () 
        else if parseInit("## ", Tag.Heading2, lines, tb) then () 
        else 
          tb.sub += TreeBuilder(Tag.Text, lines(0))
          loop(lines.drop(1), tb)
    end loop

    val result = TreeBuilder(Tag.Text, "")
    loop(s.minMargin.split("\n"), result)
    result
  end parse
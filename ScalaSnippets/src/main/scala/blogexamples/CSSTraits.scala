package blogexamples

/**
 * User: msfroh
 * Date: 12-07-19
 * Time: 9:03 PM
 */

trait CSSStyle {
  def styleElements : Map[String, String] = Map()
}

object CSSStyle {
  // Static-like method to render a CSS key/value pair
  def renderStyleElement(element : (String,String)) =
    element._1 + ": " + element._2
}

trait Bold extends CSSStyle {
  override def styleElements = super.styleElements + ("font-weight" -> "bold");
}

trait Italic extends CSSStyle {
  override def styleElements = super.styleElements + ("font-style" -> "italic")
}

trait SansSerif extends CSSStyle {
  override def styleElements = super.styleElements +
    ("font-family" -> "Helvetica, Arial, sans-serif")
}

class StyledElement(element : String) extends CSSStyle {
  override def toString = "<" + element + " style=\"" +
    styleElements.map(CSSStyle.renderStyleElement).mkString("; ") +
    "\"></" + element +">";
}

class StyleRule(rule : String) extends CSSStyle {
  override def toString = rule + " {\n    " +
    styleElements.map(CSSStyle.renderStyleElement).mkString(";\n    ") + "\n}";
}

object RunCSSStyle {
  def main(args: Array[String]) {
    println(new StyledElement("span") with Bold with Italic with SansSerif)
    println(new StyleRule(".important") with Bold with Italic with SansSerif)
  }
}
package blogexamples

/**
 * User: msfroh
 * Date: 12-07-17
 * Time: 11:50 PM
 */

class UAPExample1 {
  var name = "Michael"
}

class UAPExample2 {
  private[this] var _name = "Michael"

  def name = _name

  def name_=(name : String) {
    _name = name
  }
}

class UAPExample3 {
  private val filename = "UAPExample3.txt"

  def name = {
    import java.io.{FileReader, BufferedReader, File}
    if (new File(filename).exists()) {
      val reader = new BufferedReader(new FileReader(filename))
      try {
        reader.readLine
      } finally {
        reader.close()
      }
    } else "Michael"
  }

  def name_=(name : String) {
    import java.io.FileWriter
    val writer = new FileWriter(filename)
    try {
      writer.write(name)
    } finally {
      writer.close()
    }
  }
}

object Main {
  def main(args : Array[String]) {
    val x = new UAPExample1
    println(x.name)
    x.name = "Blah"
    println(x.name)

    val y = new UAPExample2
    println(y.name)
    y.name = "Blah"
    println(y.name)

    val z = new UAPExample3
    println(z.name)
    z.name = "Blah"
    println(z.name)
  }
}
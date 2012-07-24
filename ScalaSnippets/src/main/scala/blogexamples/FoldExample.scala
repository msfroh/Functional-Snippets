package blogexamples

/**
 * User: msfroh
 * Date: 12-07-21
 * Time: 3:13 AM
 */

object FoldExample {
  def main(args: Array[String]) {
    val list = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
    println(list.foldLeft(0)(_ * 10 + _))
  }
}
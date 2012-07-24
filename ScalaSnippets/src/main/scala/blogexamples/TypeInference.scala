package blogexamples

import collection.immutable.HashMap
/**
 * User: msfroh
 * Date: 12-07-19
 * Time: 1:04 AM
 */

class TypeInference {
  def example() {
    val magicNumber = "Magic number" // It's a string
    var myMap = new HashMap[String,Int] // It's a HashMap
    myMap += (magicNumber -> 42)
    val value = myMap(magicNumber) // It's an Int
  }

}
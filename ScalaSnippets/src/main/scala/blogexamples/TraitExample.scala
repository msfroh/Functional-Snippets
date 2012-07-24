package blogexamples

/**
 * User: msfroh
 * Date: 12-07-19
 * Time: 1:54 AM
 */

trait MyTrait {
  var mutableValue = 10
  def value : Int
  def doubleValue = value * 2 // Implementing logic
}

abstract class MyBaseClass(_value : Int) {
  val value = _value
}

abstract class MyBaseClass2(maxVal : Int) {
  def value = new java.util.Random().nextInt(maxVal + 1)
}

class MyClass(value : Int) extends MyBaseClass(value) with MyTrait

class MyClass2(maxVal : Int) extends MyBaseClass2(maxVal) with MyTrait

object RunTraitExample {
  def main(args : Array[String]) {
    val myObject = new MyClass(10)
    println(myObject.doubleValue)

    val myObject2 = new MyClass2(10)
    println(myObject2.doubleValue)

  }
}
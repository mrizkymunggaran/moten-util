

class DataStore {
     def greet() = println("hi there")
}

object DataStore{
  def main(args: Array[String]) {
      val ds = new DataStore
      ds.greet
  }
}


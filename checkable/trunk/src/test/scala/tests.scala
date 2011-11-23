package amsa {
  import org.junit.Test

  @Test
  class CheckableTest {
    @Test
    def test {
      val c = new CtsAvailCheckable()
      println(c.propertiesUrl)
      println(c())
    }
  }

}
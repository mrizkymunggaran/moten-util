package amsa {
  import org.junit.Test

  @Test
  class CheckableTest {
    @Test
    def test {
      val list = List(new CtsAvailCheckable(), new GoogleCheckable(), new SampleWebAppCheckable())
      list.foreach { case c: checkable.Checkable => { println(c); println(c()) } }
    }
  }

}
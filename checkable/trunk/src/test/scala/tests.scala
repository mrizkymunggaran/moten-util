package amsa {
  import org.junit.Test
  import amsa._

  @Test
  class CheckableTest {
    @Test
    def dummy {}
    
    def test {
      val list = List(new CtsAvailCheckable(), new GoogleCheckable(), new SampleWebAppCheckable())
      list.foreach { case c: checkable.Checkable => { println(c); println(c()) } }
    }
  }
}
package simple {
  import org.junit.Test
  import xsd.Schema
  
  @Test
  class SimpleTest {
    @Test
    def generateTestForm = {
      val visitor = new HtmlVisitor
      import scala.xml._
      val schema = scalaxb.fromXML[Schema](
        XML.load(new java.io.FileInputStream("src/test/resources/test.xsd")))

      new Simple(schema, "person", visitor).process
      println(visitor.text)
      val file = new java.io.File("target/generated-webapp/form.html")
      file.getParentFile().mkdirs
      val fos = new java.io.FileOutputStream(file);
      fos.write(visitor.text.getBytes)
      fos.close
    }
    
    
    
  }
  
}
package org.moten.david.viem

import org.junit._
import java.util.Date
import Assert._

@Test
class AppTest {

	def empty = MetaSet(Set(),MetaData("empty"))
    def create(name:String, value:String, time:Date) =
        TimedIdentifier(Identifier(IdentifierType(name),value),time)

    @Test
    def testOK() = assertTrue(true)

    @Test
    def testSystem() = {
		val date = new Date()
		val date2 = new Date(0)
		val m = "m"
		val g = "g"
		val a1 = create("m","1", date)
		val a2 = create("g","2", date)
		val b1 = create("m","1", date2)
		val b2 = create("g","3", date2)
		println(a1)
		println(a2)
		println(Set(a2,a1).max)

		//check that max works
		assertEquals(
				TimedIdentifier(Identifier(IdentifierType(m),"1"),date)                 ,
				Set(a2,a1).max)
		assertEquals(
				TimedIdentifier(Identifier(IdentifierType(m),"1"),date)                 ,
				Set(a1,a2).max)                             

		println(new System().merge(a1,a2,MetaData("a"), MetaSet(Set(b1,b2),MetaData("b")), empty))
		println("******************\nfinished tests")
	}
}



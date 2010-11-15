package viem

import org.junit._
import java.util.Date
import Assert._

@Test
class ViemTest {

	def empty = MetaSet(Set(),MetaData("empty"))
    def create(name:String, value:String, time:Date) =
        TimedIdentifier(Identifier(IdentifierType(name),value),time)
        
    @Test
    def testOK() = assertTrue(true)

    @Test
    def testSystem() = {

	    def pp(r:MergeResult):String = r.toString()
                .replace("MetaSet", "\n\tMetaSet")
                .replace("TimedIdentifier", "\n\t\t\tTimedIdentifier")
                .replace("MetaData", "\n\t\tMetaData") 
    
	    
		val date1 = new Date(100000000000L)
		val date0 = new Date(0)
		val m = "m"
		val g = "g"
		val a1 = create("a1","1", date1)
		val a2 = create("a2","2", date1)
		val b1 = create("a1","1", date0)
		val b2 = create("a2","3", date0)
		println(a1)
		println(a2)
		println(Set(a2,a1).max)

		//check that max works
		assertEquals(
				a1                 ,
				Set(a1,a2).max)
		assertEquals(
                a1                 ,
                Set(a2,a1).max)
		assertEquals(
                a2                 ,
                Set(a1,a2).min)
                                    
		val merger = new Merger();
		
		println("testing alpha")
		assertEquals(Set(a2,b2), merger.alpha(Set(a1,a2),b2))
		
		println("testing complement")
		assertEquals(Set(a1),merger.complement(Set(a1,a2),Set(a2)))
		assertEquals(Set(a2),merger.complement(Set(a1,a2),Set(a1)))
		assertEquals(Set(a1,a2),merger.complement(Set(a1,a2),Set(b1)))
		assertEquals(Set(),merger.complement(Set(a1,a2),Set(a1,a2)))
		
		println("testing typeMatch")
		assertEquals(a2,merger.typeMatch(Set(a1,a2), b2))
		assertEquals(a2,merger.typeMatch(Set(a1,a2), a2))
		
		val mda = MetaData("a")
		val mdb = MetaData("b")
		val mdc = MetaData("c")
		
		println("testing merge")
		var r = merger.merge(
		            a1,a2,mda, 
		            MetaSet(Set(b1,b2),mdb), 
		            empty)
		println(pp(r))
		
		r = merger.merge(a1,a1,mda,empty,empty)
		println(pp(r))
		assertEquals(MergeResult(MetaSet(Set(a1),mda),empty,empty,Set()),r)
		
		r = merger.merge(a1,a2,mda,empty,empty)
        println(pp(r))
        assertEquals(MergeResult(MetaSet(Set(a1,a2),mda),empty,empty,Set()),r)
		//assertEquals(MergeResult(MetaSet(Set(TimedIdentifier(a1,date), TimedIdentifier(Identifier(IdentifierType(g),3),date2)),MetaData(a)),MetaSet(Set(),MetaData(empty)),MetaSet(Set(),MetaData(empty)),Set()), r)
//		assertEquals(MergeResult(MetaSet(Set(a1,a2),MetaData("a")),empty,empty,Set()),sys)
		
		println("******************\nfinished tests")
	}
	
}


	



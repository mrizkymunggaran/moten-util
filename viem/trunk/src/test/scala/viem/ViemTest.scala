package viem

import org.junit._
import java.util.Date
import Assert._
import Predef._
import viem.Merger._

@Test
class ViemTest {

	
    def create(name:String, value:String, time:Date) =
        TimedIdentifier(Identifier(IdentifierType(name),value),time)
    
    def pp(r:MergeResult):String = r.toString()
                .replace("MetaSet", "\n\tMetaSet")
                .replace("TimedIdentifier", "\n\t\t\tTimedIdentifier")
                .replace("MetaData", "\n\t\tMetaData") 
                
    def checkEquals(expected:MergeResult, value:MergeResult)  {
	    println(pp (value))
	    if (!expected.equals(value))
	        println("expected:\n"+pp(expected))
	    assertEquals(expected, value)
	    println ("****************")
	}

	class MergeValidatorIfEqual extends MergeValidator {
	    override def mergeIsValid(a:MetaData, b:MetaData):Boolean = a==b
	}
	
	class MergeValidatorConstant(valid:Boolean) extends MergeValidator {
	    override def mergeIsValid(a:MetaData, b:MetaData):Boolean = valid
	}
	
	case class Data(name:String) extends MetaData

    @Test
    def testSystem() = {
	    
		val date1 = new Date(100000000000L) //1973
		val date0 = new Date(0) //1970
		val date = new Date(date0.getTime()-1000)
		val m = "m"
		val g = "g"
		val a1 = create("a1","1", date1)
		val a1old = create("a1","1", date0)
		val a1older = create("a1","1",date)
		val a2 = create("a2","2", date1)
		val a2old = create("a2","2", date0)
		val a2olderDiff = create("a2","4",date)
		val a3 = create("a3","1", date1)
		val a3old = create("a3","1", date0)
		val a3older = create("a3","1", date)
		
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
                                    
		var merger = new Merger(new MergeValidatorConstant(true));
		
		println("testing alpha")
		assertEquals(Set(a2,a2old), merger.alpha(Set(a1,a2),a2old))
		assertEquals(Set(a2), merger.alpha(Set(a1),a2))
		
		println("testing complement")
		assertEquals(Set(a1),merger.complement(Set(a1,a2),Set(a2)))
		assertEquals(Set(a2),merger.complement(Set(a1,a2),Set(a1)))
		assertEquals(Set(a1,a2),merger.complement(Set(a1,a2),Set(a1old)))
		assertEquals(Set(),merger.complement(Set(a1,a2),Set(a1,a2)))
		
		println("testing typeMatch")
		assertEquals(a2,merger.typeMatch(Set(a1,a2), a2old))
		assertEquals(a2,merger.typeMatch(Set(a1,a2), a2))
		
		
		
		val mda = Data("a")
		val mdb = Data("b")
		val mdc = Data("c")
		
		println("testing merge")
		var r = merger.merge(
		            a1,a2,mda, 
		            MetaSet(Set(a1old,a2old),mdb), 
		            empty)
		println(pp(r))
		
		println("add (a1) to an empty system")
		r = merger.merge(a1,a1,mda,empty,empty)
		checkEquals(MergeResult(MetaSet(Set(a1),mda),empty,empty),r)
		
		println("add (a1,a2) to an empty system")
		r = merger.merge(a1,a2,mda,empty,empty)
        checkEquals(MergeResult(MetaSet(Set(a1,a2),mda),empty,empty),r)
        
        println("add (a1) to a system with (a1)")
        r = merger.merge(a1,a1,mda,MetaSet(Set(a1),mdb),empty)
        checkEquals(MergeResult(MetaSet(Set(a1),mda),empty,empty),r)
        
        println("add (a1,a2) to a system with (a1)")
        r = merger.merge(a1,a2,mda,MetaSet(Set(a1),mdb),empty)
        checkEquals(MergeResult(MetaSet(Set(a1,a2),mda),empty,empty),r)
        
        println("add (a1,a2) to a system with (a1,a2)")
        r = merger.merge(a1,a2,mda,MetaSet(Set(a1),mdb),empty)
        checkEquals(MergeResult(MetaSet(Set(a1,a2),mda),empty,empty),r)
        
        println("add (a1) to a system with newer (a1)")
        r = merger.merge(a1old,a1old,mda,MetaSet(Set(a1),mdb),empty)
        checkEquals(MergeResult(empty, MetaSet(Set(a1),mdb),empty),r)
        
        println("add (a1, a2) to a system with (newer a1)")
        r = merger.merge(a1old,a2old,mda,MetaSet(Set(a1),mdb),empty)
        checkEquals(MergeResult(empty, MetaSet(Set(a1,a2old),mdb),empty),r)
        
        println("add (a1, a2) to a system with (newer a1,older a2)")
        r = merger.merge(a1old,a2old,mda,
                 MetaSet(Set(a1,a2olderDiff),mdb),empty)
        checkEquals(MergeResult(empty, MetaSet(Set(a1,a2old),mdb),empty),r)
        
        println("add (a1, a2) to a system with (older a1,newer a2)")
        r = merger.merge(a1old,a2old,mda,
                 MetaSet(Set(a1older,a2),mdb),empty)
        checkEquals(MergeResult(MetaSet(Set(a1old,a2),mda),empty,empty),r)
        
        println("add (a1, a2) to a system with (older a1) (older a2)")
        r = merger.merge(a1,a2,mda,
                 MetaSet(Set(a1old),mdb),MetaSet(Set(a2old),mdc))
        checkEquals(MergeResult(MetaSet(Set(a1,a2),mda),empty,empty),r)
        
        //TODO do some tests with invalid merges
        
        println("add (a1, a2) to a system with (newer a1) (newer a2)")
        r = merger.merge(a1old,a2old,mda,
                 MetaSet(Set(a1),mdb),MetaSet(Set(a2),mdc))
        checkEquals(MergeResult(empty,MetaSet(Set(a1,a2),mdb),empty),r)
        
		println("******************\nfinished tests successfully")
	}
	
}


	



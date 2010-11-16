package viem

import viem._
import org.junit.runner._
import org.junit.runner.notification._

object ViemTestMain {
  def main(args : Array[String]) : Unit = {
      
        new ViemTest().testSystem()
      
//        val junit = new JUnitCore();
//        val result = junit.run(new ViemTest().getClass())
//        if (result.getFailureCount >0) 
//            List(result.getFailures()).foreach({ println(_)}); 
//        println(result.getFailureCount + " failures out of "+ result.getRunCount + " tests")
        
        
  }
}
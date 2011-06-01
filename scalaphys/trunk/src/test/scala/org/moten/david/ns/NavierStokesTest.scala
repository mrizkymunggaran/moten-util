package org.moten.david.ns

import _root_.org.junit._
import Assert._
import scala.math._
@Test
class NavierStokesTest {

  @Test
  def testOK() = assertTrue(true)

  @Test
  def testNewtonsMethod() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 2, 0.1, 0.0001, 5) match {
      case None => fail
      case x: Some[Double] => assertEquals(9.0, x.get, 0.001)
    }
  }

}


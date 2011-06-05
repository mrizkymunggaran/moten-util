package org.moten.david.ns

import _root_.org.junit._
import Direction._
import Assert._
import scala.math._
import scala.util.Random._
import Logger._

@Test
class VectorTest {

  val Precision = 0.000001

  @Test
  def testVectorAddition() {
    val v1 = new Vector(1, 2, 3)
    val v2 = new Vector(10, 20, 30)
    assertEquals(Vector(11, 22, 33), v1 add v2)
  }

  @Test
  def testVectorSubtraction() {
    val v1 = new Vector(1, 2, 3)
    val v2 = new Vector(10, 20, 30)
    assertEquals(Vector(9, 18, 27), v2 minus v1)
    assertEquals(Vector(9, 18, 27), v2 - v1)
  }

  @Test
  def testVectorDotProduct() {
    val v1 = new Vector(1, 2, 3)
    val v2 = new Vector(10, 20, 30)
    assertEquals(140.0, v1 * v2, Precision)
  }

  @Test
  def testVectorScalaProduct() {
    val v1 = new Vector(1, 2, 3)
    assertEquals(Vector(2, 4, 6), v1 * 2)
  }

  @Test
  def testVectorScalaDivide() {
    val v1 = new Vector(1, 2, 3)
    assertEquals(Vector(1.0 / 2, 2 / 2.0, 3 / 2.0), v1 / 2)
  }

  @Test
  def testVectorSum() {
    val v1 = new Vector(1, 2, 3.5)
    assertEquals(1 + 2 + 3.5, v1 sum, Precision)
  }

  @Test
  def testVectorModify() {
    val v1 = new Vector(1, 2, 3)
    assertEquals(Vector(4, 2, 3), v1.modify(X, 4))
    assertEquals(Vector(1, 4, 3), v1.modify(Y, 4))
    assertEquals(Vector(1, 2, 4), v1.modify(Z, 4))
  }
}

@Test
class NewtonsMethodTest {

  @Test
  def testNewtonsMethodSolvesANonLinearEquation() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 2, 0.1, 0.0001, 5) match {
      case None => fail
      case x: Some[Double] => assertEquals(9.0, x.get, 0.001)
    }
  }

  @Test
  def testNewtonsMethodSolvesLinearInOneIteration() {
    def f(x: Double) = 2 * x - 3
    NewtonsMethod.solve(f, 2, 0.1, 0.0001, 1) match {
      case None => fail
      case x: Some[Double] => assertEquals(1.5, x.get, 0.001)
    }
  }

  @Test
  def testNewtonsMethodSolvesInOneIterationIfSuppliedWithAnswerAsInitialValue() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 9, 0.1, 0.0001, 1) match {
      case None => fail
      case x: Some[Double] => assertEquals(9.0, x.get, 0.001)
    }
  }

  @Test
  def testNewtonsMethodReturnsNoneIfMaxIterationsIsZeroAndIntialValueIsWrong() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 8, 0.1, 0.0001, 0) match {
      case None => Unit
      case x: Some[Double] => fail
    }
  }
  @Test
  def testNewtonsMethodReturnsCorrectAnswerIfMaxIterationsIsZeroAndIntialValueIsCorrect() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 9, 0.1, 0.0001, 0) match {
      case None => fail
      case x: Some[Double] => assertEquals(9.0, x.get, 0.001)
    }
  }
}

@Test
class NavierStokesTest {
  import Util._

  @Test
  def testNavierStokesStepDoesNothingToDataInEquilibrium() {
    info("equilibrium run")
    //create a 5x5x5 regular grid with no movement and 0 surface pressure, 
    //seawater kinematic viscosity is for 20 degrees C
    val size = 20
    info("creating map")
    val map = vectors(size).par
      .map(v => (v, Value(
        velocity = Vector.zero,
        pressure = abs(v.z * 1000 * 9.8),
        density = 1000,
        viscosity = 0.00000105,
        isWall = v.z == -size,
        isBoundary = Direction.values.map(d => (d, abs(v.get(d)) == 1 || v.get(d) == size)).toMap)))
      .seq.toMap
    info("creating Data")
    val data = new RegularGridData(map)
    //    println(data)
    val data2 = data.step(30)
    //    println(data2)
    //should be no change in any value after 30 seconds
    data.getPositions.foreach(v => assertEquals(data.getValue(v), data2.getValue(v)))
  }

  @Test
  def testNavierStokesStepDoesSomethingWhenNotInEquilibrium() {
    info("real try at solving")
    //create a 5x5x5 regular grid with no movement and 0 surface pressure, 
    //seawater kinematic viscosity is for 20 degrees C
    val size = 50
    info("creating map")
    val map = vectors(size).par
      .map(v => (v, Value(
        velocity = Vector.zero + Vector(nextDouble, nextDouble, nextDouble),
        pressure = abs(v.z * 1000 * 9.8) + 20 * nextDouble,
        density = 1000,
        viscosity = 0.00000105,
        isWall = v.z == -size,
        isBoundary = Direction.values.map(d => (d, abs(v.get(d)) == 1 || v.get(d) == size)).toMap)))
      .seq.toMap
    info("creating Data")
    val data = new RegularGridData(map)
    //    println(data)
    val data2 = data.step(30)
    //    println(data2)
  }
}

object Util {
  def vectors(size: Int) = {
    val range = Range(1, size + 1)
    (for (
      i <- range;
      j <- range;
      k <- range
    ) yield (i, j, k))
      .map(t => Vector(t._1, t._2, -t._3))
  }
}

@Test
class GridDataTest {
  import Util._

  @Test
  def testGetDirectionalNeighbours() {
    import Grid._
    val map = getDirectionalNeighbours(vectors(5).toSet)
    println(map)
    assertEquals((1.0, 3.0), map.getOrElse(X, null).getOrElse(2.0, null))
    assertEquals(None, map.getOrElse(X, null).get(5.0))
  }
}

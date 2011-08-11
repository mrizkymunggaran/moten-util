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
  import Throwing._

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
        isObstacle = v.z == -size,
        boundary = Direction.values.map(d => (d, abs(v.get(d)) == 1 || v.get(d) == size)).toMap)))
      .seq.toMap
    info("creating Data")
    val data = new RegularGridSolver(map)
    //    println(data)
    val data2 = data.step(30)
    //    println(data2)
    //should be no change in any value after 30 seconds
    data.getPositions.foreach(v => assertTrue(equals(data.getValue(v), data2.getValue(v), 0.0001)))
  }

  private def equals(v1: Value, v2: Value, precision: Double): Boolean = {
    abs(v1.pressure - v2.pressure) <= precision && equals(v1.velocity, v2.velocity, precision)
  }

  private def equals(v1: Vector, v2: Vector, precision: Double) = {
    abs(v1.x - v2.x) <= precision && abs(v1.y - v2.y) <= precision && abs(v1.z - v2.z) <= precision
  }

  @Test
  def testNavierStokesStepDoesSomethingWhenNotInEquilibrium() {
    info("real try at solving")
    //create a 5x5x5 regular grid with no movement and 0 surface pressure, 
    //seawater kinematic viscosity is for 20 degrees C
    val size = 20
    info("creating map")
    val map = vectors(size).par
      .map(v => (v, Value(
        velocity = Vector.zero + Vector(nextDouble, nextDouble, nextDouble),
        pressure = abs(v.z * 1000 * 9.8) + 20 * nextDouble,
        density = 1000,
        viscosity = 0.00000105,
        isObstacle = v.z == -size,
        boundary = Direction.values.map(d => (d, abs(v.get(d)) == 1 || v.get(d) == size)).toMap)))
      .seq.toMap
    info("creating Data")
    val data = new RegularGridSolver(map)
    //    println(data)
    val data2 = data.step(30)
    //    println(data2)
  }

  @Test
  def testNavierStokesWithOneMetreBoxAndOneSlipWallNoZComponentShouldCreateWhirlpool() {
    //from http://www.stanford.edu/class/me469b/handouts/incompressible.pdf
    val size = 20
    val vectors = vectors2D(size)
    info("vectors=" + vectors)
    val max = vectors.map(_.x).max
    val min = vectors.map(_.x).min
    val maxZ = vectors.map(_.z).max
    val minZ = vectors.map(_.z).min
    info("max=" + max)
    val map = vectors.map(v => {
      val obstacle = v.x == 0 || v.x == max || v.y == 0
      def isZBoundary(d: Direction, v: Vector) =
        (d equals Z) && (v.get(d) == minZ || v.get(d) == maxZ)
      (v, Value(
        velocity = if (v.y == max) Vector(1, 0, 0) else Vector.zero,
        pressure = abs(9.8 * 1000 * v.z),
        density = 1000,
        viscosity = 0.00000105,
        isObstacle = obstacle,
        boundary = Direction.values.map(d =>
          (d, !obstacle && (isZBoundary(d, v)
            || abs(v.get(d)) == max))).toMap))
    }).toMap
    val data = new RegularGridSolver(map)
    val v1 = Vector(1.0 / size, (size - 1.0) / size, 0.0)
    val v2 = data.getValueAfterTime(v1, 1)
    info(v2)
    assertFalse("Velocity for position should have changed",
      data.getValue(v1).velocity equals v2.velocity)
    println(data.step(1, 7))
    //    println(Range(0, 30).map(data.step(1, _).getValue(v1) + "\n"))
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

  def vectors2D(size: Int) = {
    val range = Range(0, size + 1)
    (for (
      i <- range;
      j <- range;
      k <- Range(0, 3)
    ) yield (i, j, k))
      .map(t => Vector(1.0 * t._1 / size, 1.0 * t._2 / size, -t._3))
  }
}

@Test
class GridDataTest {
  import Util._
  import Vector._
  import Grid._

  @Test
  def testGetDirectionalNeighbours() {
    val map = getDirectionalNeighbours(vectors(5).toSet)
    println(map)
    assertEquals((Some(1.0), Some(3.0)), map.getOrElse((X, 2.0), null))
    assertEquals((Some(4.0), None), map.getOrElse((X, 5.0), null))
    assertEquals((None, Some(2.0)), map.getOrElse((X, 1.0), null))
  }

  @Test
  def testDirectionalNeighboursWithOneZLayerOnly() {
    val vectors = vectors2D(size = 5)
    getDirectionalNeighbours(vectors.toSet)
  }

  @Test
  def checkDirectionalNeighboursReturnsEmptyMapOnGivenEmptySet() {
    assertEquals(Map(), getDirectionalNeighbours(Set()))
  }

  @Test
  def checkDirectionalNeighboursReturnsEmptyMapOnGivenOnePointSet() {
    assertEquals(Map(), getDirectionalNeighbours(Set(zero)))
  }
}

/**
 * P = Point
 * O = Obstacle
 * B = Boundary
 *
 * Test Cases
 *
 * PPP
 * PPO
 * PPB
 * PO*
 * PB*
 *
 * Use function values sqrt of different prime numbers
 *
 */
@Test
class RegularGridSolverTest {

  @Test
  def check() {
    val grid = Grid(Set())
    def values(v: Vector) = { null }
    val solver = new RegularGridSolver(grid, values, validate = false)
    //    override def getGradient(position: Vector, direction: Direction,
    //    f: PositionFunction, values: Vector => Value, relativeTo: Option[Vector],
    //    derivativeType: Derivative): Double = {

  }

}

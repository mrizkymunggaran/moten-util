package org.moten.david.ns

import _root_.org.junit._
import Direction._
import Assert._
import scala.math._
import scala.util.Random._
import Logger._

@Test
class NewtonsMethodTest {

  @Test
  def testNewtonsMethod() {
    def f(x: Double) = sqrt(x) - 3
    NewtonsMethod.solve(f, 2, 0.1, 0.0001, 5) match {
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
        depth = size,
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
    val size = 20
    info("creating map")
    val map = vectors(size).par
      .map(v => (v, Value(
        velocity = Vector.zero + Vector(nextDouble, nextDouble, nextDouble),
        pressure = abs(v.z * 1000 * 9.8) + 20 * nextDouble,
        depth = size,
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

/**
 * Provides a clear and concise solver for the
 * Navier Stokes equations over a rectangular
 * grid domain (regularly gridded) for an
 * incompressible liquid (sea water). Concise
 * and clear code is considered more important
 * than performance/optimisation because
 * concurrent/distributed running of the
 * routines will be used to provide performance
 * scalability.
 */
package org.moten.david.ns
import scala.collection.immutable.TreeSet

object Direction extends Enumeration {
  type Direction = Value
  val X, Y, Z = Value
  def ordered = List(X, Y, Z)
}

import Direction._

case class Vector(x: Double, y: Double, z: Double) {
  def this(list: List[Double]) {
    this(list(0), list(1), list(2))
  }
  def get(direction: Direction): Double = {
    direction match {
      case X => x
      case Y => y
      case Z => z
    }
  }
  def *(v: Vector) = x * v.x + y * v.y + z * v.z
  def *(d: Double) = Vector(x * d, y * d, z * d)
  def minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)
  def -(v: Vector) = minus(v)
  def +(v: Vector) = add(v)
  def add(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
  def /(d: Double) = Vector(x / d, y / d, z / d)
  def /(v: Vector) = Vector(x / v.x, y / v.y, z / v.z)
  def sum = x + y + z
  def modify(direction: Direction, d: Double) = {
    Vector(if (direction equals X) d else x,
      if (direction equals Y) d else y,
      if (direction equals Z) d else z)
  }
  def list = List(x, y, z)
}

object VectorUtil {
  import Double._
  def zero = Vector(0, 0, 0)
  def nan = Vector(NaN, NaN, NaN)
}

import VectorUtil._

case class Matrix(row1: Vector, row2: Vector, row3: Vector) {
  def *(v: Vector) = Vector(row1 * v, row2 * v, row3 * v)
}

case class Value(
  velocity: Vector, pressure: Double, depth: Double,
  density: Double, viscosity: Double, isWall: Boolean,
  isBoundary: Map[Direction, Boolean])

case class Entry(position: Vector, value: Value)

object Data {
  val gravity = Vector(0, 0, -9.8)
}

trait Data {
  import Data._

  //implement these!
  def getValue(vector: Vector): Value
  def getVelocityGradient(position: Vector, direction: Direction): Vector
  def getPressureGradient(position: Vector): Vector
  def getVelocityGradient2nd(position: Vector, direction: Direction): Vector
  def getPressureGradient2nd(position: Vector): Vector

  //implemented for you
  def getVelocityLaplacian(position: Vector, direction: Direction) = {
    getVelocityGradient2nd(position, direction).sum
  }
  def getVelocityLaplacian(position: Vector): Vector = {
    Vector(getVelocityLaplacian(position, X),
      getVelocityLaplacian(position, Y),
      getVelocityLaplacian(position, Z))
  }
  def getVelocityJacobian(position: Vector) = {
    Matrix(getVelocityGradient(position, X),
      getVelocityGradient(position, Y),
      getVelocityGradient(position, Z))
  }
  def dvdt(position: Vector) = {
    val value = getValue(position)
    val velocityLaplacian: Vector = getVelocityLaplacian(position)
    val pressureGradient: Vector = getPressureGradient(position)
    val velocityJacobian: Matrix = getVelocityJacobian(position)

    ((velocityLaplacian * value.viscosity minus pressureGradient) / value.density)
      .minus(gravity)
      .minus(velocityJacobian * value.velocity)
  }

  def valueAfterTimeStep(position: Vector, timeStep: Double) = {
    getValue(position).velocity.add(dvdt(position) * timeStep)
  }
}

object RegularGridData {
  //TODO unit test this
  def getDirectionalNeighbours(vectors: Set[Vector]) = {
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values
    Direction.values.map(d => (d, vectors.map(_.get(d))
      .toSet.toList.sorted.sliding(3).toList.map(x => (x(1), (x(0), x(2)))).toMap)).toMap
  }
}

class RegularGridData(map: Map[Vector, Value]) extends Data {
  import Data._
  import RegularGridData._
  val FIRST = true
  val SECOND = false

  val ordinates = getDirectionalNeighbours(map.keySet)

  def getValue(vector: Vector): Value = {
    map.get(vector) match {
      case s: Some[Value] => s.get
      case None => throw new RuntimeException("no value exists for position " + vector)
    }
  }

  def getNeighbours(position: Vector, d: Direction): Tuple2[Vector, Vector] = {
    val t = ordinates.getOrElse(d, null).get(position.get(d)).getOrElse(null)
    (position.modify(d, t._1),
      position.modify(d, t._2))
  }

  def getPressureGradient(position: Vector) = {
    new Vector(Direction.ordered.map(getPressureGradient(position, _)))
  }

  private def getPressureGradient(position: Vector, direction: Direction): Double = {
    val value = getValue(position);
    val force = gravity.get(direction) * value.density
    getGradient(position, direction, force, 0, getValue(_).pressure, FIRST)
  }

  def getPressureGradient2nd(position: Vector): Vector = {
    new Vector(Direction.ordered.map(d => getGradient(position, d, 0, 0, getValue(_).pressure, SECOND)))
  }

  def getVelocityGradient(position: Vector, direction: Direction): Vector = {
    new Vector(Direction.ordered.map(d => getGradient(position, direction, 0, 0, getValue(_).velocity.get(d), FIRST)))
  }

  def getVelocityGradient2nd(position: Vector, direction: Direction): Vector = {
    new Vector(Direction.ordered.map(d => getGradient(position, direction, 0, 0, getValue(_).velocity.get(d), SECOND)))
  }

  private type Pair = Tuple2[Double, Double]

  private def getGradient(a1: Pair, a: Pair, a2: Pair, isFirstDerivative: Boolean): Double = {
    if (isFirstDerivative)
      (a2._2 - a1._2) / (a2._1 - a1._1)
    else
      (a2._2 + a1._2 - 2 * a._2) / (a2._1 - a1._1)
  }

  private def getGradient(position: Vector, direction: Direction,
    wallGradient: Double, boundaryGradient: Double, f: Vector => Double, isFirstDerivative: Boolean): Double = {
    val value = getValue(position)
    if (value.isWall)
      return wallGradient
    else if (value.isBoundary.get(direction) match {
      case v: Some[Boolean] => v.get
      case None => throw new RuntimeException("boundary info not found")
    })
      return boundaryGradient
    else {
      val n = getNeighbours(position, direction)
      return getGradient(
        (n._1.get(direction), f(n._1)),
        (position.get(direction), f(position)),
        (n._2.get(direction), f(n._2)),
        isFirstDerivative)
    }
  }
}

class NavierStokes {
  def step(data: Data, timestep: Double): Data = data
}
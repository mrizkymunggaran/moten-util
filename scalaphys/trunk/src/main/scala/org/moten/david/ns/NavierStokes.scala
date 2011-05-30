package org.moten.david.ns
import VectorUtil._
import scala.collection.immutable.TreeSet

object Direction extends Enumeration {
  type Direction = Value
  val X, Y, Z = Value
  def ordered = List(X, Y, Z)
}

import Direction._

case class Vector(x: Double, y: Double, z: Double) {
  def this(seq: List[Double]) {
    this(seq(0), seq(1), seq(2))
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
}

object VectorUtil {
  import Double._
  def zero = Vector(0, 0, 0)
  def nan = Vector(NaN, NaN, NaN)
}

object Data {
  val gravity = Vector(0, 0, -9.8)
}

case class Matrix(row1: Vector, row2: Vector, row3: Vector) {
  def *(v: Vector) = Vector(row1 * v, row2 * v, row3 * v)
}

case class Value(
  velocity: Vector, pressure: Double, depth: Double,
  density: Double, viscosity: Double, isWall: Boolean,
  isBoundary: Map[Direction, Boolean])

case class Entry(position: Vector, value: Value)

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
  val ordinates = RegularGridData.getDirectionalNeighbours(map.keySet)
  println(ordinates)

  def getValue(vector: Vector): Value = {
    map.get(vector) match {
      case s: Some[Value] => s.get
      case None => throw new RuntimeException("no value exists for position")
    }
  }

  def getVelocityGradient(position: Vector, direction: Direction): Vector = {
    val value = getValue(position)
    if (value.isWall)
      zero
    else if (value.isBoundary.get(direction) match {
      case v: Some[Boolean] => v.get
      case None => throw new RuntimeException("boundary info not found")
    })
      zero
    else {
      val n = getNeighbours(position, direction);
      getVelocityGradient(position, n)
    }
  }

  def getNeighbours(position: Vector, d: Direction): Tuple2[Vector, Vector] = {
    val t = ordinates.getOrElse(d, null).get(position.get(d)).getOrElse(null)
    (position.modify(d, t._1),
      position.modify(d, t._2))
  }

  private def getVelocityGradient(position: Vector, n: Tuple2[Vector, Vector]): Vector = {
    val a1 = n._1
    val a2 = n._2
    val v1 = getValue(a1)
    val v2 = getValue(a2)
    //TODO calculate for irregular grid spacing
    (v2.velocity - v1.velocity) / (a2 - a1)
  }

  private def getPressureGradient(position: Vector, n: Tuple2[Vector, Vector], direction: Direction) = {
    val a1 = n._1
    val a2 = n._2
    val v1 = getValue(a1)
    val v2 = getValue(a2)
    //TODO calculate for irregular grid spacing
    (v2.pressure - v1.pressure) / (a2 - a1).get(direction)
  }

  def getPressureGradient(position: Vector) = {
    val list: List[Double] = Direction.ordered.map(getPressureGradient(position, _))
    new Vector(list)
  }

  private def getPressureGradient(position: Vector, direction: Direction): Double = {
    val value = getValue(position)
    if (value.isWall)
      return gravity.get(direction) * value.density
    else if (value.isBoundary.get(direction) match {
      case v: Some[Boolean] => v.get
      case None => throw new RuntimeException("boundary info not found")
    })
      return 0
    else {
      val n = getNeighbours(position, direction);
      getPressureGradient(position, n, direction)
    }
  }

  private def getVelocityGradient2nd(position: Vector, n: Tuple2[Vector, Vector]): Vector = {
    val a1 = n._1
    val a2 = n._2
    val v1 = getValue(a1)
    val v2 = getValue(a2)
    val v = getValue(position)
    //TODO calculate for irregular grid spacing
    (v2.velocity + v1.velocity - v.velocity * 2) / (a2 - a1)
  }

  private def getPressureGradient2nd(position: Vector, n: Tuple2[Vector, Vector], direction: Direction): Double = {
    val a1 = n._1
    val a2 = n._2
    val v1 = getValue(a1)
    val v2 = getValue(a2)
    //TODO calculate for irregular grid spacing
    (v2.pressure - v1.pressure) / (a2 - a1).get(direction)
  }

  def getVelocityGradient2nd(position: Vector, direction: Direction): Vector = {
    val value = getValue(position)
    if (value.isWall)
      return zero
    else if (value.isBoundary.get(direction) match {
      case v: Some[Boolean] => v.get
      case None => throw new RuntimeException("boundary info not found")
    })
      return zero
    else {
      val n = getNeighbours(position, direction);
      return getVelocityGradient2nd(position, n)
    }
  }

  private def getPressureGradient2nd(position: Vector, direction: Direction): Double = {
    val value = getValue(position)
    if (value.isWall)
      return gravity.get(direction) * value.density
    else if (value.isBoundary.get(direction) match {
      case v: Some[Boolean] => v.get
      case None => throw new RuntimeException("boundary info not found")
    })
      return 0
    else {
      val n = getNeighbours(position, direction);
      getPressureGradient(position, n, direction)
    }
  }

  def getPressureGradient2nd(position: Vector): Vector = {
    val list: List[Double] = Direction.ordered.map(getPressureGradient2nd(position, _))
    new Vector(list)
  }

}

class NavierStokes {
  def step(data: Data, timestep: Double): Data = data
}

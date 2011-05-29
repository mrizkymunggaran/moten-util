package org.moten.david.ns
import VectorUtil._
import scala.collection.immutable.TreeSet

object Direction extends Enumeration {
  type Direction = Value
  val X, Y, Z = Value
}

import Direction._

case class Vector(x: Double, y: Double, z: Double) {
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
  def add(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
  def /(d: Double) = Vector(x / d, y / d, z / d)
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

case class Matrix(row1: Vector, row2: Vector, row3: Vector) {
  def *(v: Vector) = Vector(row1 * v, row2 * v, row3 * v)
}

case class Value(
  velocity: Vector, pressure: Double, depth: Double,
  density: Double, viscosity: Double, isWall: Boolean,
  isBoundary: Map[Direction, Boolean])

case class Entry(position: Vector, value: Value)

trait Data {
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
    val gravity = Vector(0, 0, 9.8)

    ((velocityLaplacian * value.viscosity minus pressureGradient) / value.density)
      .add(gravity)
      .minus(velocityJacobian * value.velocity)
  }

  def valueAfterTimeStep(position: Vector, timeStep: Double) = {
    getValue(position).velocity.add(dvdt(position) * timeStep)
  }
}

object IrregularGridData {
  //TODO unit test this
  def getDirectionalNeighbours(vectors: Set[Vector]) = {
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values
    Direction.values.map(d => (d, vectors.map(_.get(d))
      .toSet.toList.sorted.sliding(3).toList.map(x => (x(1), (x(0), x(2)))).toMap)).toMap
  }
}

class IrregularGridData(map: Map[Vector, Value]) extends Data {

  val ordinates = IrregularGridData.getDirectionalNeighbours(map.keySet)
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
      val n = getNeighbours(position);
      getVelocityGradient(position, n)
    }
  }

  def getNeighbours(position: Vector): Neighbours = {
    Direction.values.map(d => (d, ordinates.getOrElse(d, null).get(position.get(d))))
      .map(t => {
        val d = t._1; val pair = t._2;
        (d, (position.modify(d, pair._1), position.modify(d, pair._2)))
      })
    null
  }

  def getVelocityGradient(position: Vector, n: Neighbours): Vector = {
    //TODO
    position
  }
  def getPressureGradient(position: Vector): Vector = position
  def getVelocityGradient2nd(position: Vector, direction: Direction): Vector = position
  def getPressureGradient2nd(position: Vector): Vector = position
}

case class Neighbours(
  entries: Map[Direction, Tuple2[Option[Entry], Option[Entry]]])

class NavierStokes {
  def step(data: Data, timestep: Double): Data = data
}


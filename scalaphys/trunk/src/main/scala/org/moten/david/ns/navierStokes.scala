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
import java.util.Date
import java.text.SimpleDateFormat

object Logger {
  val df = new SimpleDateFormat("HH:mm:ss.SSS")
  var infoEnabled = true
  var debugEnabled = false
  def info(msg: => AnyRef) = if (infoEnabled) println(df.format(new Date()) + " " + msg)
  def debug(msg: => AnyRef) = if (debugEnabled) println(df.format(new Date()) + " " + msg)
}
import Logger._

/**
 * X,Y horizontal coordinates (arbitrary coordinate system).
 * Z is height above sea level in m (all calculations
 * assume SI units).
 *
 */
object Direction extends Enumeration {
  type Direction = Value
  val X, Y, Z = Value
  def directions = List(X, Y, Z)
}
import Direction._

/**
 * The derivative type.
 */
object DerivativeType extends Enumeration {
  type Derivative = Value
  val FirstDerivative, SecondDerivative = Value
}
import DerivativeType._

/**
 * A mathematical vector in X,Y,Z space.
 *
 * @param x
 * @param y
 * @param z
 */
case class Vector(x: Double, y: Double, z: Double) {
  def this(list: List[Double]) {
    this(list(0), list(1), list(2))
  }
  def this(t: Tuple3[Double, Double, Double]) {
    this(t._1, t._2, t._3)
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

object Vector {
  import Double._
  def zero = Vector(0, 0, 0)
}

import Vector._

/**
 * An ordering to help with readable String representations of Vector collections.
 */
object VectorOrdering extends Ordering[Vector] {
  def compare(a: Vector, b: Vector): Int = {
    if (a.z != b.z) return a.z compare b.z
    else if (a.y != b.y) return a.y compare b.y
    else return a.x compare b.x
  }
}

/**
 * A 3 dimensional matrix.
 */
case class Matrix(row1: Vector, row2: Vector, row3: Vector) {
  def *(v: Vector) = Vector(row1 * v, row2 * v, row3 * v)
}

case class Value(
  velocity: Vector, pressure: Double, depth: Double,
  density: Double, viscosity: Double, isWall: Boolean,
  isBoundary: Map[Direction, Boolean]) {
  def modifyPressure(p: Double) = {
    Value(velocity, p, depth, density, viscosity, isWall, isBoundary)
  }
}

object Data {
  val gravity = Vector(0, 0, -9.8)
}

trait Data {
  import Data._

  //implement these!
  def getPositions: Set[Vector]
  def getValue(vector: Vector): Value
  def getGradient(position: Vector, direction: Direction,
    wallGradient: Double, boundaryGradient: Double,
    f: Vector => Double, derivativeType: Derivative): Double
  def step(timestep: Double): Data

  //implemented for you
  private def getVelocityLaplacian(position: Vector, direction: Direction) =
    getVelocityGradient2nd(position, direction).sum

  private def getVelocityLaplacian(position: Vector): Vector =
    Vector(getVelocityLaplacian(position, X),
      getVelocityLaplacian(position, Y),
      getVelocityLaplacian(position, Z))

  private def getVelocityJacobian(position: Vector) =
    Matrix(getVelocityGradient(position, X),
      getVelocityGradient(position, Y),
      getVelocityGradient(position, Z))

  /**
   * See http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates.
   * @param position
   * @return
   */
  private def dvdt(position: Vector) = {
    val value = getValue(position)
    val velocityLaplacian: Vector = getVelocityLaplacian(position)
    val pressureGradient: Vector = getPressureGradient(position)
    val velocityJacobian: Matrix = getVelocityJacobian(position)
    val divergenceOfStress = velocityLaplacian * value.viscosity minus pressureGradient
    debug("velocityLaplacian" + velocityLaplacian)
    debug("pressureGradient=" + pressureGradient)
    debug("velocityJacobian=" + velocityJacobian)
    debug("divergenceOfStress=" + divergenceOfStress)

    val result = ((divergenceOfStress) / value.density)
      .add(gravity)
      .minus(velocityJacobian * value.velocity)
    debug("dvdt=" + result)
    result
  }

  private def getPressureLaplacian(position: Vector) =
    getPressureGradient2nd(position).sum

  private def getVelocityAfterTimeStep(position: Vector, timeStep: Double) =
    getValue(position).velocity.add(dvdt(position) * timeStep)

  /**
   * Returns the Conservation of Mass (Continuity) Equation described by the
   * Navier-Stokes equations.
   */
  private def getPressureCorrection(position: Vector, v1: Vector, timeDelta: Double)(pressure: Double): Double = {
    val velocityNext = getVelocityAfterTimeStep(position, timeDelta)
    val v = getValue(position)
    //assume not wall or boundary
    val valueNext = Value(velocityNext, pressure, v.depth, v.density, v.viscosity, true, Map(X -> false, Y -> false, Z -> false))
    val dataWithOverridenPressureAtPosition = new DataOverride(this, position, valueNext)
    dataWithOverridenPressureAtPosition.getPressureCorrection(position)
  }

  private def getPressureCorrection(position: Vector): Double = {
    val value = getValue(position)
    val pressureLaplacian = getPressureLaplacian(position)
    def f(v: Vector, direction: Direction) =
      getVelocityGradient(v, direction) * v
    return pressureLaplacian +
      directions.map(d => getGradient(position, d, 0, 0, f(_, d), FirstDerivative)).sum
  }

  /**
   * See http://en.wikipedia.org/wiki/Pressure-correction_method
   */
  def getValueAfterTime(position: Vector, timeDelta: Double): Value = {
    debug("getting value after time at " + position)
    val value0 = getValue(position)
    debug("value0=" + value0)
    if (value0.isWall) return value0
    val v1 = getVelocityAfterTimeStep(position, timeDelta)
    debug("v1=" + v1)
    val f = getPressureCorrection(position, v1, timeDelta)(_)
    val h = 0.1
    val precision = 0.0001
    val maxIterations = 15
    val newPressure = NewtonsMethod.solve(f, value0.pressure, h, precision, maxIterations) match {
      case None => value0.pressure
      case a: Some[Double] => a.get
    }
    debug("newPressure=" + newPressure + "old=" + value0.pressure)
    return value0.modifyPressure(newPressure)
  }

  private def getPressureGradient(position: Vector): Vector =
    new Vector(directions.map(getPressureGradient(position, _)))

  private def getPressureGradient(position: Vector, direction: Direction): Double = {
    val value = getValue(position);
    val force = gravity.get(direction) * value.density
    getGradient(position, direction, force, force, getValue(_).pressure, FirstDerivative)
  }

  private def getPressureGradient2nd(position: Vector): Vector =
    new Vector(directions.map(d => getGradient(position, d, 0, 0, getValue(_).pressure, SecondDerivative)))

  private def getVelocityGradient(position: Vector, direction: Direction): Vector =
    new Vector(directions.map(d => getGradient(position, direction, 0, 0, getValue(_).velocity.get(d), FirstDerivative)))

  private def getVelocityGradient2nd(position: Vector, direction: Direction): Vector =
    new Vector(directions.map(d => getGradient(position, direction, 0, 0, getValue(_).velocity.get(d), SecondDerivative)))

  private def step(data: Data, timestep: Double, numSteps: Int): Data = {
    if (numSteps == 0) return data
    else return step(step(timestep), timestep, numSteps - 1)
  }

  def step(timestep: Double, numSteps: Int): Data =
    step(this, timestep, numSteps)

  override def toString = getPositions.toList.sorted(VectorOrdering)
    .map(v => (v, getValue(v)).toString + "\n").toString
}

private class DataOverride(data: Data, position: Vector, value: Value) extends Data {
  override def getPositions(): Set[Vector] = data.getPositions
  override def getValue(vector: Vector): Value = if (vector equals position) value else data.getValue(vector)
  override def getGradient(position: Vector, direction: Direction,
    wallGradient: Double, boundaryGradient: Double,
    f: Vector => Double, derivativeType: Derivative): Double =
    data.getGradient(position, direction, wallGradient, boundaryGradient, f, derivativeType)
  override def step(timestep: Double): Data = data.step(timestep)
}

object Grid {
  //TODO unit test this
  def getDirectionalNeighbours(vectors: Set[Vector]) = {
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values. This 
    //map will return None for all elements on the boundary.
    Direction.values.map(d => (d, vectors.map(_.get(d))
      .toSet.toList.sorted.sliding(3)
      .toList.map(x => (x(1), (x(0), x(2)))).toMap)).toMap
  }
}

/**
 * Implements gradient calculation for a regular grid. Every position
 * on the grid has nominated neighbours to be used in gradient
 * calculations (both first and second derivatives).
 */
class RegularGridData(map: Map[Vector, Value]) extends Data {
  import Data._
  import Grid._

  private val ordinates = getDirectionalNeighbours(map.keySet)

  override def getValue(vector: Vector): Value = {
    map.get(vector) match {
      case s: Some[Value] => s.get
      case None => throw new RuntimeException("no value exists for position " + vector)
    }
  }

  override def getPositions = map.keySet

  override def getGradient(position: Vector, direction: Direction,
    wallGradient: Double, boundaryGradient: Double, f: Vector => Double, derivativeType: Derivative): Double = {
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
      getGradient(
        (n._1.get(direction), f(n._1)),
        (position.get(direction), f(position)),
        (n._2.get(direction), f(n._2)),
        derivativeType)
    }
  }

  private def getNeighbours(position: Vector, d: Direction): Tuple2[Vector, Vector] = {
    val t = ordinates.getOrElse(d, null).get(position.get(d)).getOrElse(null)
    (position.modify(d, t._1), position.modify(d, t._2))
  }

  private type Pair = Tuple2[Double, Double]

  private def getGradient(a1: Pair, a: Pair, a2: Pair, derivativeType: Derivative): Double = {
    if (derivativeType equals FirstDerivative)
      (a2._2 - a1._2) / (a2._1 - a1._1)
    else
      (a2._2 + a1._2 - 2 * a._2) / (a2._1 - a1._1)
  }

  override def step(timestep: Double): Data = {
    info("creating parallel collection")
    val vectors = map.keySet.par
    info("solving timestep")
    val stepped = vectors.map(v => (v, getValueAfterTime(v, timestep)))
    info("converting to sequential collection")
    val seq = stepped.seq
    info("converting to map")
    val newMap = seq.toMap
    info("creating new Data")
    return new RegularGridData(newMap)
  }

}

/**
 * Newton's Method solver for one dimensional equations in the real numbers.
 *
 */
object NewtonsMethod {
  import scala.math._

  /**
   * Uses Newton's Method to solve f(x) = 0 for x. Returns None
   * if no solution found within maxIterations. This method uses
   * recursion so a large number of maxIterations could use a lot
   * of stack space.
   *
   * @param f
   * @param x initial guess at the solution.
   * @param h the delta for calculation of derivative
   * @param precision the desired maximum absolute value of f(x) at an
   *        acceptable solution
   * @param maxIterations the maximum number of iterations to perform.
   *        If maxIterations is reached then returns
   * @return optional solution
   */
  def solve(f: Double => Double, x: Double, h: Double,
    precision: Double, maxIterations: Long): Option[Double] = {
    val fx = f(x)
    if (abs(fx) <= precision) Some(x)
    else if (maxIterations == 0) None
    else {
      val gradient = (f(x + h) - fx) / h
      if (gradient == 0) None
      else solve(f, x - fx / gradient, h, precision, maxIterations - 1)
    }
  }
}
/**
 * Provides a clear and concise solver for the
 * Navier Stokes equations over a rectangular
 * grid domain (regularly gridded) for an
 * incompressible liquid (sea water). Concise
 * and clear code is considered more important
 * than performance because concurrent/
 * distributed running of the routines will be
 *  used to provide performance scalability.
 */
package org.moten.david.ns

import scala.collection.immutable.TreeSet
import java.util.Date
import java.text.SimpleDateFormat

/**
 * Logs to System.out with a timestamp.
 */
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
  /**
   * Returns the value of the vector for the given direction.
   *
   * @param direction
   * @return
   */
  def get(direction: Direction): Double = {
    direction match {
      case X => x
      case Y => y
      case Z => z
    }
  }
  /**
   * Returns the dot product with another `Vector`.
   * @param v
   * @return
   */
  def *(v: Vector) = x * v.x + y * v.y + z * v.z

  /**
   * Returns the scalar product of this with a `Double` value.
   * @param d
   * @return
   */
  def *(d: Double) = Vector(x * d, y * d, z * d)

  /**
   * Returns difference of this with the given `Vector`.
   * @param v
   * @return
   */
  def minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)

  /**
   * Returns difference of this with the given `Vector`.
   * @param v
   * @return
   */
  def -(v: Vector) = minus(v)

  /**
   * Returns the sum of this and the given `Vector`.
   * @param v
   * @return
   */
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

/**
 * Companion object for Vector}.
 */
object Vector {
  import Double._
  def zero = Vector(0, 0, 0)
}

import Vector._

/**
 * An ordering to help with readable String
 * representations of Vector collections.
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

/**
 * Measures the velocity and pressure field and water
 * properties for a given position.
 */
case class Value(
  velocity: Vector, pressure: Double,
  density: Double, viscosity: Double, isObstacle: Boolean,
  boundary: Map[Direction, Boolean]) {
  /**
   * Returns a copy of this with pressure modified.
   * @param p
   * @return
   */
  def modifyPressure(p: Double) = {
    Value(velocity, p, density, viscosity, isObstacle, boundary)
  }
  /**
   * Returns a copy of this with velocity modified.
   * @param vel
   * @return
   */
  def modifyVelocity(vel: Vector) = {
    Value(vel, pressure, density, viscosity, isObstacle, boundary)
  }

  def isBoundary(direction: Direction): Boolean = {
    boundary.get(direction) match {
      case None => throw new RuntimeException("boundary info not found")
      case Some(x) => x
    }
  }
}

/**
 * Companion object for `Data`.
 */
object Data {
  /**
   * Acceleration due to gravity. Note that this vector
   * determines the meaning of the Z direction (positive Z
   * direction is decrease in depth).
   */
  val gravity = Vector(0, 0, -9.8)
}

/**
 * Positions, values and methods for the numerical Navier Stokes equation solver.
 */
trait Data {
  import Data._

  //implement these abstract methods!
  /**
   * Returns all positions.
   * @return
   */
  def getPositions: Set[Vector]

  /**
   * Returns `Value` at a position.
   * @param vector
   * @return
   */
  def getValue(position: Vector): Value

  /**
   * Returns the gradient of the function f with respect to direction at the given position.
   * @param position
   * @param direction
   * @param f
   * @param derivativeType
   * @return
   */
  def getGradient(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector],
    derivativeType: Derivative): Double

  /**
   * Returns calculated `Data` after timestep seconds.
   * @param timestep
   * @return
   */
  def step(timestep: Double): Data

  //implemented for you
  private def getVelocityLaplacian(position: Vector, direction: Direction) =
    getVelocityGradient2nd(position, direction).sum

  private def getVelocityLaplacian(position: Vector): Vector =
    Vector(getVelocityLaplacian(position, X),
      getVelocityLaplacian(position, Y),
      getVelocityLaplacian(position, Z))

  private def getVelocityJacobian(position: Vector) =
    Matrix(getVelocityGradient(position, X, None),
      getVelocityGradient(position, Y, None),
      getVelocityGradient(position, Z, None))

  /**
   * Returns the derivative of velocity over time using this
   * {http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates formula}.
   * @param position
   * @return
   */
  private def dvdt(position: Vector) = {
    val value = getValue(position)
    val velocityLaplacian: Vector = getVelocityLaplacian(position)
    val pressureGradient: Vector = getPressureGradient(position)
    val velocityJacobian: Matrix = getVelocityJacobian(position)
    val divergenceOfStress =
      velocityLaplacian * value.viscosity minus pressureGradient
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
  private def getPressureCorrection(position: Vector, v1: Vector,
    timeDelta: Double)(pressure: Double): Double = {
    val v = getValue(position)
    //assume not obstacle or boundary
    val valueNext = v.modifyPressure(pressure)
    val dataWithOverridenPressureAtPosition =
      new DataOverride(this, position, valueNext)
    dataWithOverridenPressureAtPosition.getPressureCorrection(position)
  }

  private def getPressureCorrection(position: Vector): Double = {
    val value = getValue(position)
    val pressureLaplacian = getPressureLaplacian(position)
    return pressureLaplacian +
      directions.map(d => getGradient(position, d,
        gradientDot(d, Some(position)), None, FirstDerivative)).sum
  }

  private def gradientDot(direction: Direction,
    relativeTo: Option[Vector])(v: Vector) =
    getVelocityGradient(v, direction, relativeTo) * v

  /**
   * Returns the` Value` at the given position after `timeDelta` in seconds
   * by solving <a href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates">
   * a 3D formulation of the Navier-Stokes equations</a>.  After the velocity
   *  calculation a pressure correction is performed according to this
   * <a href="http://en.wikipedia.org/wiki/Pressure-correction_method">method</a>.
   */
  def getValueAfterTime(position: Vector, timeDelta: Double): Value = {
    debug("getting value after time at " + position)
    val value0 = getValue(position)
    debug("value0=" + value0)
    if (value0.isObstacle) return value0
    val v1 = getVelocityAfterTimeStep(position, timeDelta)
    debug("v1=" + v1)
    val f = getPressureCorrection(position, v1, timeDelta)(_)
    //TODO what values for h,precision?
    val h = 1
    val precision = 0.000001
    val maxIterations = 15
    val newPressure = NewtonsMethod.solve(f, value0.pressure, h,
      precision, maxIterations) match {
        case None => value0.pressure
        case a: Some[Double] => if (a.get < 0) value0.pressure else a.get
      }
    debug("newPressure=" + newPressure + "old=" + value0.pressure)
    return value0.modifyPressure(newPressure).modifyVelocity(v1)
  }

  private def getPressureGradient(position: Vector): Vector =
    new Vector(directions.map(getPressureGradient(position, _)))

  private def getPressureGradient(position: Vector, direction: Direction): Double = {
    val value = getValue(position);
    val force = gravity.get(direction) * value.density
    getGradient(position, direction,
      getValue(_).pressure, None, FirstDerivative)
  }

  private def getPressureGradient2nd(position: Vector): Vector =
    new Vector(directions.map(d =>
      getGradient(position, d, getValue(_).pressure, None, SecondDerivative)))

  private def getVelocityGradient(position: Vector, direction: Direction, relativeTo: Option[Vector]): Vector =
    new Vector(directions.map(d =>
      getGradient(position, direction,
        getValue(_).velocity.get(d), relativeTo, FirstDerivative)))

  private def getVelocityGradient2nd(position: Vector, direction: Direction): Vector =
    new Vector(directions.map(d =>
      getGradient(position, direction,
        getValue(_).velocity.get(d), None, SecondDerivative)))

  private def step(data: Data, timestep: Double, numSteps: Int): Data = {
    if (numSteps == 0) return data
    else return step(data.step(timestep), timestep, numSteps - 1)
  }

  def step(timestep: Double, numSteps: Int): Data =
    step(this, timestep, numSteps)

  override def toString = getPositions.toList.sorted(VectorOrdering)
    .map(v => (v, getValue(v)).toString + "\n").toString
}

/**
 * Returns a copy of {{data}} with the value at position overriden. Uses facade pattern.
 */
private class DataOverride(data: Data, position: Vector,
  value: Value) extends Data {
  override def getPositions(): Set[Vector] = data.getPositions
  override def getValue(vector: Vector): Value =
    if (vector equals position) value else data.getValue(vector)
  override def getGradient(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector], derivativeType: Derivative): Double =
    data.getGradient(position, direction, f, relativeTo, derivativeType)
  override def step(timestep: Double): Data = data.step(timestep)
}

object Grid {

  def getDirectionalNeighbours(vectors: Set[Vector]): Map[Direction, Map[Double, (Double, Double)]] = {
    info("getting directional neighbours")
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values. This 
    //map will return None for all elements on the boundary.
    directions.map(d => (d, vectors.map(_.get(d))
      .toSet.toList.sorted.sliding(3).toList
      .flatMap(x =>
        if (x.size < 3) List()
        else List((x(1), (x(0), x(2))))).toMap)).toMap
  }

  def getDirectionalNeighbourOptions(vectors: Set[Vector]): Map[(Direction, Double), (Option[Double], Option[Double])] = {
    info("getting directional neighbours")
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values. This 
    //map will return None for all elements on the boundary.
    directions.map(d => {
      val b = vectors.map(_.get(d)).toSet.toList.sorted
      if (b.size < 3)
        List()
      else
        b.sliding(3).toList.map(
          x => ((d, x(1)), (Some(x(0)), Some(x(2)))))
          .++(List(((d, b(0)), (None, Some(b(1))))))
          .++(List(((d, b(b.size - 1)), (Some(b(b.size - 2)), None))))
    }).flatten.toMap
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
  private val neighbours = getDirectionalNeighbourOptions(map.keySet)

  override def getValue(vector: Vector): Value = {
    map.get(vector) match {
      case s: Some[Value] => s.get
      case None => throw new RuntimeException("no value exists for position " + vector)
    }
  }

  override def getPositions = map.keySet

  //TODO test this
  private def getGradient(position: Vector, direction: Direction,
    n: Tuple2[Vector, Vector], f: Vector => Double,
    derivativeType: Derivative): Double = {

    getGradient(
      (n._1.get(direction), f(n._1)),
      (position.get(direction), f(position)),
      (n._2.get(direction), f(n._2)),
      derivativeType)
  }

  override def getGradient(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector],
    derivativeType: Derivative): Double = {

    val value = getValue(position)
    if (value.isObstacle)
      relativeTo match {
        case None => throw new RuntimeException("relativeTo must be supplied as a non-empty parameter if obstacle gradient is being calculated")
        case Some(x) => {
          //TODO
          //get the neighbour in direction closest to relativeTo
          return 0
        }
      }
    else if (value.isBoundary(direction))
      //TODO
      return 0
    else {
      val n = getNeighbours(position, direction)
      getGradient(position, direction, n, f, derivativeType)
    }
  }

  private def getNeighbours(position: Vector, d: Direction): Tuple2[Vector, Vector] = {
    val t = ordinates.getOrElse(d, null).get(position.get(d)).getOrElse(null)
    (position.modify(d, t._1), position.modify(d, t._2))
  }

  private type Pair = Tuple2[Double, Double]

  private def getGradient(a1: Pair, a: Pair, a2: Pair,
    derivativeType: Derivative): Double = {
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
  import scala.annotation._

  /**
   * Uses Newton's Method to solve f(x) = 0 for x. Returns `None`
   * if no solution found within maxIterations. This method uses
   * tail recursion optimisation so a large number of maxIterations
   * will not cause a stack overflow.
   *
   * @param f function to find roots of (where f(x)=0)
   * @param x initial guess at the solution.
   * @param h the delta for calculation of derivative
   * @param precision the desired maximum absolute value of f(x) at an
   *        acceptable solution
   * @param maxIterations the maximum number of iterations to perform.
   *        If maxIterations is reached then returns `None`
   * @return optional solution
   */
  @tailrec
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
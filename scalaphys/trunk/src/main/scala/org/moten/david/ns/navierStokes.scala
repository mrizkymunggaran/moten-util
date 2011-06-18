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

/**
 * Logs to System.out with a timestamp.
 */
object Logger {
  import java.util.Date
  import java.text.SimpleDateFormat
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

  def setNotBoundaryOrObstacle =
    Value(velocity, pressure, density, viscosity, false, Value.NoBoundary)
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
  def isBoundaryOrObstacle(direction: Direction) =
    isObstacle || isBoundary(direction)
}

object Value {
  val NoBoundary = Map(X -> false, Y -> false, Z -> false)
}

trait DataFunction {
  def apply(data: Data, v: Vector): Double
}

object PressureFunction extends DataFunction {
  def apply(data: Data, v: Vector): Double = {
    data.getValue(v).pressure
  }
}

class VelocityFunction(direction: Direction) extends DataFunction {
  def apply(data: Data, v: Vector): Double = {
    data.getValue(v).velocity.get(direction)
  }
}

object VelocityFunction {
  private val map = directions.map(d => (d, new VelocityFunction(d))).toMap

  def get(direction: Direction) =
    map.getOrElse(direction, null)
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

  /**
   * ************************************************
   * Implement these
   * ************************************************
   */

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
   * Returns the gradient of the function f with respect to direction at the
   * given position. `relativeTo` is used to calculate the gradient near and
   *  on boundary and obstacle positions. In particular we need to handle
   *  the case when the obstacle is of width one in a given direction and
   *  thus has non-obstacle neighbours on both sides. The proxy field values
   *  applied to the obstacle will depend the side of interest.
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

  /**
   * ************************************************
   * Implemented for you
   * ************************************************
   */

  /**
   * Returns the Laplacian of the velocity vector in the given direction.
   * @param position
   * @param direction
   * @return
   */
  private def getVelocityLaplacian(position: Vector, direction: Direction) =
    getVelocityGradient2nd(position, direction).sum

  /**
   * Returns the Laplacian of the velocity vector as a vector.
   * @param position
   * @return
   */
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

  /**
   * Returns the Laplacian of pressure at position which in 3D is:
   * dp2/d2x + dp2/d2y + dp2/d2z.
   * @param position
   * @return
   */
  private def getPressureLaplacian(position: Vector) =
    getPressureGradient2nd(position).sum

  /**
   * Returns the velocity vector after time timeDelta seconds.
   * @param position
   * @param timeStep
   * @return
   */
  private def getVelocityAfterTime(position: Vector, timeDelta: Double) =
    getValue(position).velocity.add(dvdt(position) * timeDelta)

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
      new DataOverride(this, x => if (x equals position) Some(valueNext) else None)
    dataWithOverridenPressureAtPosition.getPressureCorrection(position)
  }

  /**
   * Returns the value of the pressure correction function at position.
   * @param position
   * @return
   */
  private def getPressureCorrection(position: Vector): Double = {
    val value = getValue(position)
    val pressureLaplacian = getPressureLaplacian(position)
    return pressureLaplacian +
      directions.map(d => getGradient(position, d,
        gradientDot(d, Some(position)), None, FirstDerivative)).sum
  }

  /**
   * Returns the value of Del(Del dot v) for a given velocity vector v.
   * @param direction
   * @param relativeTo
   * @param v
   * @return
   */
  def gradientDot(direction: Direction,
    relativeTo: Option[Vector])(v: Vector): Double =
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
    val v1 = getVelocityAfterTime(position, timeDelta)
    debug("v1=" + v1)
    val f = getPressureCorrection(position, v1, timeDelta)(_)
    //TODO what values for h,precision?
    val h = 1
    val precision = 0.000001
    val maxIterations = 15
    val newPressure = NewtonsMethod.solve(f, value0.pressure, h,
      precision, maxIterations) match {
        case None => value0.pressure
        case Some(a) => if (a < 0) value0.pressure else a
      }
    debug("newPressure=" + newPressure + "old=" + value0.pressure)
    return value0.modifyPressure(newPressure).modifyVelocity(v1)
  }

  /**
   * Returns the pressure gradient vector at position.
   * @param position
   * @return
   */
  private def getPressureGradient(position: Vector): Vector =
    new Vector(directions.map(getPressureGradient(position, _)))

  /**
   * Returns the pressure gradient at position in a given direction.
   * @param position
   * @param direction
   * @return
   */
  private def getPressureGradient(position: Vector, direction: Direction): Double = {
    val value = getValue(position);
    val force = gravity.get(direction) * value.density
    getGradient(position, direction,
      getValue(_).pressure, None, FirstDerivative)
  }

  /**
   * Returns the second derivative pressure gradient at position.
   * @param position
   * @return
   */
  private def getPressureGradient2nd(position: Vector): Vector =
    new Vector(directions.map(d =>
      getGradient(position, d, getValue(_).pressure, None, SecondDerivative)))

  /**
   * Returns the gradient of the velocity vector at position in the given direction
   * and for the purposes of obstacle gradient calculation includes the
   * relativeTo position so a neighbour in the direction of relativeTo can be
   * chosen paired with the position of the obstacle itself for the gradient
   * calculation.
   * @param position
   * @param direction
   * @param relativeTo
   * @return
   */
  private def getVelocityGradient(position: Vector, direction: Direction,
    relativeTo: Option[Vector]): Vector =
    new Vector(directions.map(d =>
      getGradient(position, direction,
        getValue(_).velocity.get(d), relativeTo, FirstDerivative)))

  /**
   * Returns the gradient of the pressure gradient at position in the
   * given direction.
   * @param position
   * @param direction
   * @return
   */
  private def getVelocityGradient2nd(position: Vector,
    direction: Direction): Vector =
    new Vector(directions.map(d =>
      getGradient(position, direction,
        getValue(_).velocity.get(d), None, SecondDerivative)))

  /**
   * Returns a new immutable Data object representing the
   * state of the system after `timestep` seconds.
   * @param data
   * @param timestep
   * @param numSteps
   * @return
   */
  private def step(data: Data, timestep: Double, numSteps: Int): Data = {
    if (numSteps == 0) return data
    else return step(data.step(timestep), timestep, numSteps - 1)
  }

  /**
   * Returns a new immutable Data object after repeating the
   *  timestep `numSteps` times.
   * @param timestep
   * @param numSteps
   * @return
   */
  def step(timestep: Double, numSteps: Int): Data =
    step(this, timestep, numSteps)

  /**
   * Returns a readable view of the positions and their values.
   * @return
   */
  override def toString = getPositions.toList.sorted(VectorOrdering)
    .map(v => (v, getValue(v)).toString + "\n").toString
}

/**
 * Returns a copy of {{data}} with the value at position overriden. Uses facade pattern.
 */
private case class DataOverride(data: Data, f: Vector => Option[Value]) extends Data {
  override def getPositions(): Set[Vector] = data.getPositions
  override def getValue(vector: Vector): Value =
    f(vector) match {
      case Some(v) => v
      case None => data.getValue(vector)
    }
  override def getGradient(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector], derivativeType: Derivative): Double =
    data.getGradient(position, direction, f, relativeTo, derivativeType)
  override def step(timestep: Double): Data = data.step(timestep)
}

/**
 * Utility methods for a Grid of 3D points.
 */
object Grid {

  def getDirectionalNeighbours(vectors: Set[Vector]): Map[(Direction, Double), (Option[Double], Option[Double])] = {
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

class RichTuple2[A](t: Tuple2[A, A]) {
  def map[B](f: A => B): Tuple2[B, B] = (f(t._1), f(t._2))
  def exists(f: A => Boolean) = f(t._1) || f(t._2)
}

object RichTuple2 {
  implicit def toRichTuple[A](t: Tuple2[A, A]) = new RichTuple2(t)
}

/**
 * Implements gradient calculation for a regular grid. Every positionA,
 * on the grid has nominated neighbours to be used in gradient
 * calculations (both first and second derivatives).
 */
class RegularGridData(map: Map[Vector, Value]) extends Data {
  import Data._
  import Grid._
  import RichTuple2._
  import scala.math._

  private final val neighbours = getDirectionalNeighbours(map.keySet)

  override def getValue(vector: Vector): Value = {
    map.get(vector) match {
      case s: Some[Value] => s.get
      case None => throw new RuntimeException("no value exists for position " + vector)
    }
  }

  override def getPositions = map.keySet

  //TODO test this
  private def getGradient(position: Vector, direction: Direction,
    n: Tuple2[Option[Double], Option[Double]], f: Vector => Double,
    derivativeType: Derivative): Double = {

    val t: Tuple2[Double, Double] = n match {
      case (Some(n1), Some(n2)) => (n1, n2)
      case (None, Some(n2)) => unexpected
      case (Some(n1), None) => unexpected
      case _ => unexpected
    }
    getGradient(
      (t._1, f(position.modify(direction, t._1))),
      (position.get(direction), f(position)),
      (t._2, f(position.modify(direction, t._2))),
      derivativeType)

  }

  private def unexpected =
    throw new RuntimeException("program should not get to this point")

  private def unexpected(message: String) =
    throw new RuntimeException(message)

  override def getGradient(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector],
    derivativeType: Derivative): Double = {

    val value = getValue(position)
    if (value.isObstacle)
      return getGradientAtObstacle(position, direction, f, relativeTo, derivativeType)
    else if (value.isBoundary(direction))
      //TODO boundary gradient is always 0 (yep for pressure, velocity nope
      return 0;
    else {
      val n = getNeighbours(position, direction)
      implicit def value(x: Option[Double]): Value =
        getValue(position.modify(direction, x.getOrElse(unexpected)))
      val v = n.map(value)
      val nv = n.map(x => (x.getOrElse(unexpected), value(x)))
      if (v.exists(_.isBoundaryOrObstacle(direction))) {

        //if one neighbour is obstacle or boundary then call getGradient on 
        //same new Data which overrides the Values at the neighbour positions
        //to indicate that they are NOT obstacles or boundaries (to terminate 
        //the recursion) and follow the following rules:
        //
        //if neighbour is obstacle then neighbour Value has zero velocity
        //and equal pressure except for z direction which has pressure
        //to give -9.8 derivative.
        //
        //if neighbour is boundary then neighbour Value has it's velocity
        //and pressure except for the z direction which has pressure to 
        //give -9.8 derivative
        val nv2 = nv.map(convertNeighbourValueOf(position, direction, _))
        val data = DataOverride(this, p =>
          if (p equals nv2._1._1)
            Some(nv2._1._2)
          else if (p equals nv2._2._1)
            Some(nv2._2._2)
          else
            Some(getValue(p)))
        return data.getGradient(position, direction, f, relativeTo, derivativeType)
      } else
        return getGradient(position, direction, n, f, derivativeType)
    }
  }

  private def convertNeighbourValueOf(position: Vector,
    direction: Direction,
    n: Tuple2[Double, Value]): Tuple2[Vector, Value] =
    {
      val value = getValue(position)
      val neighbour = position.modify(direction, n._1)
      if (n._2.isObstacle) {
        val value2 = value
          .modifyVelocity(zero)
          .modifyPressure(
            if (direction equals Z)
              value.pressure - 9.8 * (n._1 - position.get(direction))
            else
              value.pressure)
            .setNotBoundaryOrObstacle
        return (neighbour, value2)
      } else if (n._2 isBoundary (direction)) {
        val value2 = value
          .modifyPressure(
            if (direction equals Z)
              value.pressure - 9.8 * (n._1 - position.get(direction))
            else
              n._2.pressure)
            .setNotBoundaryOrObstacle
        return (neighbour, value2)
      } else
        return (neighbour, n._2)
    }

  private def getGradientAtObstacle(position: Vector, direction: Direction,
    f: Vector => Double, relativeTo: Option[Vector],
    derivativeType: Derivative): Double = {
    relativeTo match {
      case None => unexpected("""relativeTo must be supplied as a 
 non-empty parameter if obstacle/boundary gradient is being calculated""")
      case Some(x) => {
        //get the neighbour in direction closest to relativeTo
        val n = getNeighbours(position, direction)
        val sign = signum(x.get(direction) - position.get(direction))
        val n2 = if (sign < 0)
          (n._1, Some(position.get(direction)))
        else
          (Some(position.get(direction)), n._2)
        return getGradient(position, direction, n2, f, derivativeType)
      }
    }
  }

  private def getNeighbours(position: Vector, d: Direction): Tuple2[Option[Double], Option[Double]] = {
    neighbours.getOrElse((d, position.get(d)), unexpected)
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
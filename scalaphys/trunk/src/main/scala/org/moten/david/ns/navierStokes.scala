/////////////////////////////////////////////////////////////////////
// Navier Stokes Solver                                                                        
/////////////////////////////////////////////////////////////////////
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

                       
/////////////////////////////////////////////////////////////////////

/**
 * Logs to System.out with a timestamp.
 */
object Logger {
  import java.util.Date
  import java.text.SimpleDateFormat
  val df = new SimpleDateFormat("HH:mm:ss.SSS")
  var infoEnabled = true
  var debugEnabled = false
  def info(msg: => AnyRef) = if (infoEnabled)
    println(df.format(new Date()) + " " + msg)
  def debug(msg: => AnyRef) = if (debugEnabled)
    println(df.format(new Date()) + " " + msg)
}
import Logger._

/**
 * Useful exceptions.
 */
object Throwing {
  def unexpected =
    throw new RuntimeException(
      "program should not get to this point")

  def unexpected(message: String) =
    throw new RuntimeException(message)

  def todo =
    throw new RuntimeException("not implemented, TODO")
}

import Throwing._

/////////////////////////////////////////////////////////////////////
// Direction                                                                        
/////////////////////////////////////////////////////////////////////
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

/////////////////////////////////////////////////////////////////////
// Derivative
/////////////////////////////////////////////////////////////////////
/**
 * The derivative type.
 */
object DerivativeType extends Enumeration {
  type Derivative = Value
  val FirstDerivative, SecondDerivative = Value
}
import DerivativeType._

/////////////////////////////////////////////////////////////////////
// Vector                                
/////////////////////////////////////////////////////////////////////

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
  def ===(v: Vector) = this equals v
  def list = List(x, y, z)
}

/**
 * Companion object for Vector.
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
object HasPositionOrdering extends Ordering[HasPosition] {
  override def compare(a: HasPosition, b: HasPosition): Int =
    if (a.position.z != b.position.z)
      return a.position.z compare b.position.z
    else if (a.position.y != b.position.y)
      return a.position.y compare b.position.y
    else
      return a.position.x compare b.position.x
}

/**
 * A 3 dimensional matrix.
 */
case class Matrix(row1: Vector, row2: Vector, row3: Vector) {
  def *(v: Vector) =
    Vector(row1 * v, row2 * v, row3 * v)
}

/////////////////////////////////////////////////////////////////////
// Value                                                                        
/////////////////////////////////////////////////////////////////////

trait HasPosition {
  def position: Vector
}

trait HasValue extends HasPosition {
  def value: Value
}

case class Boundary(value: Value)
  extends HasValue {
  val position = value.position
}

case class Point(value: Value)
  extends HasValue {
  val position = value.position
}

case class Obstacle(position: Vector)
  extends HasPosition

case class Empty(position: Vector) extends HasPosition {
  def this() {
    this(Vector.zero)
  }
}

/**
 * Measures the velocity and pressure field and water
 * properties for a given position.
 */
case class Value(position: Vector,
  velocity: Vector, pressure: Double,
  density: Double, viscosity: Double)
  extends HasPosition with HasValue {

  val value = this

  /**
   * Returns a copy of this with pressure modified.
   * @param p
   * @return
   */
  def modifyPressure(p: Double) =
    new Value(position, velocity, p, density, viscosity)

  /**
   * Returns a copy of this with velocity modified.
   * @param vel
   * @return
   */
  def modifyVelocity(vel: Vector) =
    new Value(position, vel, pressure, density, viscosity)

  def modifyPosition(pos: Vector) =
    new Value(pos, velocity, pressure, density, viscosity)

}

/**
 * Companion object.
 */
object Value {
  implicit def toValue(v: HasValue) = v.value
  implicit def toPosition(p: HasPosition) = p.position
  def isObstacle(v: Value) = v.isInstanceOf[Obstacle]
}

/////////////////////////////////////////////////////////////////////
// Solver                      
/////////////////////////////////////////////////////////////////////

/**
 * Companion object for `Solver`.
 */
object Solver {
  /**
   * Acceleration due to gravity. Note that this vector
   * determines the meaning of the Z direction (positive Z
   * direction is decrease in depth).
   */
  val gravity = Vector(0, 0, -9.8)

  /**
   * Returns the value of a function of interest on the
   *  Position/Value field
   */
  type ValueFunction = HasValue => Double

}

/**
 * Positions, values and methods for the numerical Navier
 * Stokes equation solver.
 */
trait Solver {
  import Solver._
  import Value._

  /**
   * ************************************************
   * Implement these
   * ************************************************
   */

  /**
   * Returns all positions.
   * @return
   */
  def getPositions: Set[HasPosition]

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
  def getGradient(position: HasPosition, direction: Direction,
    f: ValueFunction,
    relativeTo: Option[Vector],
    derivativeType: Derivative, overrideValue: Option[HasValue]): Double

  /**
   * Returns calculated `Solver` after timestep seconds.
   * @param timestep
   * @return
   */
  def step(timestep: Double): Solver

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
  private def getVelocityLaplacian(position: HasValue, direction: Direction) =
    getVelocityGradient2nd(position, direction).sum

  /**
   * Returns the Laplacian of the velocity vector as a vector.
   * @param position
   * @return
   */
  private def getVelocityLaplacian(position: HasValue): Vector =
    Vector(getVelocityLaplacian(position, X),
      getVelocityLaplacian(position, Y),
      getVelocityLaplacian(position, Z))

  /**
   * Returns the Jacobian of velocity at a position.
   * @param position
   * @return
   */
  private def getVelocityJacobian(position: HasValue) =
    Matrix(getVelocityGradient(position, X, None),
      getVelocityGradient(position, Y, None),
      getVelocityGradient(position, Z, None))

  /**
   * Returns the derivative of velocity over time using this
   * {http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates
   *  formula}.
   * @param position
   * @return
   */
  private def dvdt(position: HasValue) = {
    val value = position.value
    val velocityLaplacian: Vector = getVelocityLaplacian(position)
    val pressureGradient: Vector = getPressureGradient(position)
    val velocityJacobian: Matrix = getVelocityJacobian(position)
    val divergenceOfStress =
      velocityLaplacian * value.value.viscosity minus pressureGradient
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
  private def getPressureLaplacian(position: HasValue, overrideValue: HasValue) =
    getPressureGradient2nd(position, overrideValue).sum

  /**
   * Returns the velocity vector after time timeDelta seconds.
   * @param position
   * @param timeStep
   * @return
   */
  private def getVelocityAfterTime(position: HasValue, timeDelta: Double) =
    position.value.velocity.add(dvdt(position) * timeDelta)

  /**
   * Returns the Conservation of Mass (Continuity) Equation described by the
   * Navier-Stokes equations.
   */
  private def getPressureCorrection(position: HasValue, v1: Vector,
    timeDelta: Double)(pressure: Double): Double = {
    val v = position.value
    //assume not obstacle or boundary
    val overrideValue = v.modifyPressure(pressure)
    getPressureCorrection(position, overrideValue)
  }

  /**
   * Returns the value of the pressure correction function at position.
   * @param position
   * @return
   */
  private def getPressureCorrection(position: HasValue, overrideValue: HasValue): Double = {
    val pressureLaplacian = getPressureLaplacian(position, overrideValue)
    return pressureLaplacian +
      directions.map(d => getGradient(position, d,
        gradientDot(d, relativeTo = Some(position.position)),
        None, FirstDerivative, Some(overrideValue))).sum
  }

  /**
   * Returns the value of Del(Del dot v) for a given position.
   * @param direction
   * @param relativeTo
   * @param v
   * @return
   */
  def gradientDot(direction: Direction,
    relativeTo: Option[Vector])(position: HasValue): Double =
    getVelocityGradient(position, direction, relativeTo) * position.velocity

  /**
   * Returns the` Value` at the given position after `timeDelta` in seconds
   * by solving
   * <a href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates">
   * a 3D formulation of the Navier-Stokes equations</a>.  After the velocity
   *  calculation a pressure correction is performed according to this
   * <a href="http://en.wikipedia.org/wiki/Pressure-correction_method">method</a>.
   */
  def getValueAfterTime(position: HasPosition, timeDelta: Double): HasPosition = {
    position match {
      case v: HasValue => getValueAfterTime(v, timeDelta)
      case _ => position
    }
  }

  /**
   * Returns the` Value` at the given position after `timeDelta` in seconds
   * by solving
   * <a href="http://en.wikipedia.org/wiki/Navier%E2%80%93Stokes_equations#Cartesian_coordinates">
   * a 3D formulation of the Navier-Stokes equations</a>.  After the velocity
   *  calculation a pressure correction is performed according to this
   * <a href="http://en.wikipedia.org/wiki/Pressure-correction_method">method</a>.
   */
  private def getValueAfterTime(position: HasValue, timeDelta: Double): HasValue = {
    debug("getting value after time at " + position)
    val value = position.value
    debug("value=" + value)

    val v1 = getVelocityAfterTime(position, timeDelta)
    debug("v1=" + v1)
    val f = getPressureCorrection(position, v1, timeDelta)(_)
    //TODO what values for h,precision?
    val h = 1
    val precision = 0.000001
    val maxIterations = 15
    val newPressure = NewtonsMethod.solve(f, value.pressure, h,
      precision, maxIterations) match {
        case None => value.pressure
        case Some(a) => if (a < 0) value.pressure else a
      }
    debug("newPressure=" + newPressure + "old=" + value.pressure)
    return value.value.modifyPressure(newPressure).modifyVelocity(v1)
  }

  /**
   * Returns the pressure gradient vector at position.
   * @param position
   * @return
   */
  private def getPressureGradient(position: HasValue): Vector =
    new Vector(directions.map(getPressureGradient(position, _)))

  /**
   * Returns the pressure gradient at position in a given direction.
   * @param position
   * @param direction
   * @return
   */
  private def getPressureGradient(position: HasValue,
    direction: Direction): Double = {
    val value = position.value;
    getGradient(position, direction,
      (p: HasValue) => p.pressure,
      None, FirstDerivative, None)
  }

  /**
   * Returns the second derivative pressure gradient at position.
   * @param position
   * @return
   */
  private def getPressureGradient2nd(position: HasValue, overrideValue: HasValue): Vector =
    new Vector(directions.map(d =>
      getGradient(position, d,
        (p: HasValue) => p.pressure,
        None, SecondDerivative, Some(overrideValue))))

  /**
   * Returns the gradient of the velocity vector at position in the given
   * direction and for the purposes of obstacle gradient calculation includes the
   * relativeTo position so a neighbour in the direction of relativeTo can be
   * chosen paired with the position of the obstacle itself for the gradient
   * calculation.
   * @param position
   * @param direction
   * @param relativeTo
   * @return
   */
  private def getVelocityGradient(position: HasValue, direction: Direction,
    relativeTo: Option[Vector]): Vector = {
    def velocity(d: Direction) = (p: HasValue) => p.value.velocity.get(d)
    new Vector(directions.map(
      d => getGradient(position, direction, velocity(d),
        relativeTo, FirstDerivative, None)))
  }

  /**
   * Returns the gradient of the pressure gradient at position in the
   * given direction.
   * @param position
   * @param direction
   * @return
   */
  private def getVelocityGradient2nd(position: HasValue,
    direction: Direction): Vector = {
    def velocity(d: Direction) = (p: HasValue) => p.velocity.get(d)
    new Vector(directions.map(d =>
      getGradient(position, direction, velocity(d),
        None, SecondDerivative, None)))
  }

  /**
   * Returns a new immutable Solver object representing the
   * state of the system after `timestep` seconds.
   * @param solver
   * @param timestep
   * @param numSteps
   * @return
   */
  private def step(solver: Solver, timestep: Double, numSteps: Int): Solver =
    if (numSteps == 0) return solver
    else return step(solver.step(timestep), timestep, numSteps - 1)

  /**
   * Returns a new immutable Solver object after repeating the
   *  timestep `numSteps` times.
   * @param timestep
   * @param numSteps
   * @return
   */
  def step(timestep: Double, numSteps: Int): Solver =
    step(this, timestep, numSteps)

  /**
   * Returns a readable view of the positions and their values.
   * @return
   */
  override def toString = getPositions.toList.sorted(HasPositionOrdering)
    .map(v => v.toString + "\n").toString
}


/////////////////////////////////////////////////////////////////////
// Grid                      
/////////////////////////////////////////////////////////////////////

/**
 * Utility methods for a Grid of 3D points.
 */
object Grid {

  type DirectionalNeighbours = Map[(Direction, HasPosition), (HasPosition, HasPosition)]

  /**
   * Returns the neighbours of an ordinate in a given direction.
   * @param vectors
   * @return
   */
  def getDirectionalNeighbours(
    positions: Set[HasPosition]): DirectionalNeighbours = {
    info("getting directional neighbours")
    //produce a map of Direction to a map of ordinate values with their 
    //negative and positive direction neighbour ordinate values. This 
    //map will return None for all elements on the boundary.
    directions.map(d => {
      val b = positions.toSet.toList.sorted(HasPositionOrdering)
      if (b.size < 3)
        List()
      else
        b.sliding(3).toList.map(
          x => ((d, x(1)), (x(0), x(2))))
          .++(List(((d, b(0)), (Empty(b(0).position), b(1)))))
          .++(List(((d, b(b.size - 1)), (b(b.size - 2), Empty(b(b.size - 1).position)))))
    }).flatten.toMap
  }

  /**
   * Returns the boundary ordinates of the vectors set which is assumed
   *  to be a 3D grid.
   * @param vectors
   * @return
   */
//TODO is this used?
  def getExtremes(vectors: Set[Vector]): Direction => (Double, Double) =
    directions.map(d => {
      val list = vectors.map(_.get(d)).toList
      (d, (list.min, list.max))
    }).toMap.getOrElse(_, unexpected)
}

/**
 * Regular or irregular grid of 3D points (vectors).
 */
case class Grid(positions: Set[HasPosition]) {
  val neighbours = Grid.getDirectionalNeighbours(positions)
}

/////////////////////////////////////////////////////////////////////
// RegularGridSolver                                                                        
/////////////////////////////////////////////////////////////////////

trait Sign
case class PositiveSign() extends Sign
case class NegativeSign() extends Sign
case class ZeroSign() extends Sign

object Sign {
  val Positive = PositiveSign()
  val Negative = NegativeSign()
  val Zero = ZeroSign();
}

object RegularGridSolver {
  import scala.math.signum
  import Solver._
  import Value._

  type O = Obstacle
  type P = Point
  type A = HasPosition
  type B = Boundary
  type E = Empty
  type V = HasValue //either Point or Boundary
  type Positions = Tuple3[HasPosition,HasPosition,HasPosition]

  def getNeighbours(grid: Grid, position: HasPosition,
    d: Direction, relativeTo: Option[Vector]): Positions =
    todo

  def overrideValue(t:HasPosition, overrideValue:Value):HasPosition = {
    if (t.position.equals(overrideValue.position))
      overrideValue
    else 
      t
  }

  def overrideValue(t:Positions, v : HasValue):Positions =
    (overrideValue(t._1,v),
     overrideValue(t._2,v),
     overrideValue(t._3,v))

  def overrideValue(t:Positions, v : Option[HasValue]):Positions =
    v match {
      case Some(value:HasValue) => overrideValue(t,value)
      case None => t
    }

  def getGradient(grid: Grid, position: HasPosition, direction: Direction,
    f: ValueFunction, relativeTo: Option[Vector], derivativeType: Derivative,
    overridden:Option[HasValue]):Double = {
    
    val n = overrideValue(
      getNeighbours(grid, position, direction, relativeTo),overridden)
    getGradient(f, n._1, n._2, n._3, direction, relativeTo, derivativeType)
  }
    
  def getGradient(f: ValueFunction,
    v1: HasPosition, v2: HasPosition, v3: HasPosition, direction: Direction,
    relativeTo: Option[Vector], derivativeType: Derivative): Double = {

    val sign = getSign(v2, relativeTo, direction)

    val t = transform((v1, v2, v3, sign))

    t match {
      case v: (V, V, V) =>
        getGradient(f, v._1, v._2, v._3, direction, derivativeType)
      case v: (V, V, E) =>
        getGradient(f, v._1, v._2, direction, derivativeType)
      case _ => unexpected
    }
  }

  /**
   * Returns either (V,V,V) or (V,V,E).
   *
   * @param t
   * @return
   */
  private def transform(
    t: (HasPosition, HasPosition, HasPosition, Sign)): (HasPosition, HasPosition, HasPosition) = {

    //sign = ZeroSign  if no relativeTo and v2 is Point
    //sign= PositiveSign if relativeTo is on the v3 side 
    //sign = NegativeSign if relativeTo is on the v1 side

    t match {
      case v: (V, V, V, Sign) => (v._1, v._2, v._3)
      case v: (V, V, E, Sign) => (v._1, v._2, v._3)
      case v: (E, V, V, Sign) => (v._2, v._3, v._1)
      case v: (A, V, O, Sign) => transform((v._1, v._2, obstacleToPoint(v._3, v._2), v._4))
      case v: (O, V, A, Sign) => transform((obstacleToPoint(v._1, v._2), v._2, v._3, v._4))
      case v: (A, O, V, PositiveSign) => (obstacleToPoint(v._2, v._3), v._3, new Empty)
      case v: (V, O, A, NegativeSign) => (v._1, obstacleToPoint(v._2, v._1), new Empty)
      case _ => unexpected
    }
  }

  private def obstacleToPoint(o: Obstacle, point: HasValue): Point = {
    return Point(point.value.modifyVelocity(Vector.zero).modifyPosition(o.position))
  }

  private def getGradient(f: HasValue => Double,
    p1: HasValue, p2: HasValue, p3: HasValue,
    direction: Direction,
    derivativeType: Derivative): Double = {
    derivativeType match {
      case FirstDerivative =>
        (f(p3) - f(p1)) / (p3.position.get(direction) - p1.position.get(direction))
      case SecondDerivative =>
        (f(p3) + f(p1) - 2 * f(p2)) / (p3.position.get(direction) - p1.position.get(direction))
      case _ => unexpected
    }
  }

  private def getGradient(f: HasValue => Double,
    p1: HasValue, p2: HasValue,
    direction: Direction,
    derivativeType: Derivative): Double = {
    derivativeType match {
      case FirstDerivative =>
        (f(p2) - f(p1)) / (p2.position.get(direction) - p1.position.get(direction))
      case SecondDerivative =>
        0
      case _ => unexpected
    }
  }

  private def getSign(x: HasPosition, 
    relativeTo: Option[Vector], direction: Direction): Sign = {
    relativeTo match {
      case None => if (!Point.getClass.isInstance(x))
        throw new RuntimeException(
          "relativeTo must be specified if " 
          + "calculating gradient at an obstacle or boundary")
      else Sign.Zero
      case Some(v: Vector) =>
        if (signum(x.position.get(direction) - v.get(direction)) > 0)
          Sign.Positive
        else
          Sign.Negative
    }
  }
}

/**
 * Implements gradient calculation for a regular grid. Every positionA,
 * on the grid has nominated neighbours to be used in gradient
 * calculations (both first and second derivatives).
 */
class RegularGridSolver(positions: Set[HasPosition], validate: Boolean) extends Solver {
  import Solver._
  import Grid._
  import scala.math._
  import RegularGridSolver._

  private final val grid = new Grid(positions)

  if (validate)
    info("validated")

  def this(positions: Set[HasPosition]) =
    this(positions, true);

  override def getPositions = grid.positions

  override def getGradient(position: HasPosition, direction: Direction,
    f: ValueFunction, relativeTo: Option[Vector],
    derivativeType: Derivative, overrideValue: Option[HasValue]): Double =
    return RegularGridSolver.getGradient(grid, position, direction,
      f, relativeTo, derivativeType,overrideValue);

  override def step(timestep: Double): Solver = {
    info("creating parallel collection")
    val collection = grid.positions //.par
    info("solving timestep")
    val stepped = collection.map(getValueAfterTime(_, timestep))
    info("converting to sequential collection")
    val seq = stepped.seq

    info("creating new Solver")
    return new RegularGridSolver(
      seq, false)
  }
}

/////////////////////////////////////////////////////////////////////
// Newtons Method                                                                        
/////////////////////////////////////////////////////////////////////

/**
 * Newton's Method solver for one dimensional equations in
 *  the real numbers.
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

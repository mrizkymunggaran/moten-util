
object Direction extends Enumeration {
  type Direction = Value
  val X,Y,Z = Value
}

import Direction._

case class Vector(x:Double, y:Double, z:Double) {
  def get(direction:Direction):Double = {
    direction match {
      case X => x
      case Y => y
      case Z => z 
    }
  }
  def *(v:Vector) = x*v.x + y*v.y + z*v.z
  def *(d:Double) = Vector(x*d,y*d,z*d)
  def minus(v:Vector) = Vector(x-v.x,y-v.y,z-v.z)
  def add(v:Vector) = Vector(x+v.x,y+v.y,z+v.z)
  def /(d:Double) = Vector(x/d,y/d,z/d)
  def sum = x + y + z
}

object VectorUtil {
  def zero = Vector(0,0,0)
}

import VectorUtil._

case class Matrix(row1:Vector,row2:Vector,row3:Vector){
  def *(v:Vector) = Vector(row1*v, row2*v, row3*v)
}

case class Value (
  velocity:Vector, pressure:Double, depth:Double, 
  density: Double, viscosity:Double, isWall:Boolean, 
  isBoundary:Map[Direction,Boolean]
)

case class Entry (position:Vector, value:Value)

trait Data {
  //implement these!
  def value(vector:Vector):Value
  def velocityGradient(position:Vector,direction:Direction):Vector
  def pressureGradient(position:Vector):Vector
  def velocityGradient2nd(position:Vector,direction:Direction):Vector
  def pressureGradient2nd(position:Vector):Vector

  //these methods are implemented
  def pressure:Vector=>Double = value(_).pressure 
  def velocity:Vector=>Vector = value(_).velocity
  def velocity(direction:Direction):Vector=>Double 
      = value(_).velocity.get(direction)
  
  def velocityLaplacian(position:Vector,direction:Direction)
      = velocityGradient2nd(position,direction).sum
  def velocityLaplacian(position:Vector):Vector  
      = Vector(velocityLaplacian(position,X),
               velocityLaplacian(position,Y),
               velocityLaplacian(position,Z)
               )   
  def velocityJacobian(position:Vector)
      = Matrix(velocityGradient(position,X),
               velocityGradient(position,Y),
               velocityGradient(position,Z))
  
  def dvdt(position:Vector)={
    val v:Value = value(position)
    val vLaplacian:Vector = velocityLaplacian(position)
    val pGradient:Vector = pressureGradient(position)
    val vJacobian:Matrix = velocityJacobian(position)
    val gravity = Vector(0,0,9.8)

   ((vLaplacian*v.viscosity minus pGradient)/v.density).
      add (gravity).
      minus (vJacobian * v.velocity)
  }

  def valueAfterTimeStep(position:Vector,timeStep:Double) 
       =  value(position).velocity.add(dvdt(position)*timeStep)
}

class IrregularGridData(map:Map[Vector,Value]) extends Data {
  def value(vector:Vector):Value = { 
      val v = map.get(vector)
      v match {
        case s:Some[Value] => s.get
        case None => throw new RuntimeException("no value exists for position")
      }
  }
  
  def velocityGradient(position:Vector,direction:Direction):Vector = position
  def pressureGradient(position:Vector):Vector = position
  def velocityGradient2nd(position:Vector,direction:Direction):Vector = position
  def pressureGradient2nd(position:Vector):Vector = position
}

case class Neighbours( 
    x1:Entry,x2:Entry,
    y1:Entry,y2:Entry,
    z1:Entry,z2:Entry)

class NavierStokes {
  def step(data:Data,timestep:Double):Data = data 
}



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
  def gradient(f:Vector=>Double, position:Vector):Vector
  def gradient2nd(f:Vector=>Double, position:Vector):Vector  

  //these methods are implemented
  def pressure:Vector=>Double = value(_).pressure 
  def velocity:Vector=>Vector = value(_).velocity
  def velocity(direction:Direction):Vector=>Double 
      = value(_).velocity.get(direction)
  
  def laplacian(f:Vector=>Double, position:Vector)
      = gradient2nd(f,position).sum
  def velocityLaplacian(position:Vector):Vector  
      = Vector(laplacian(velocity(_).x,position),
               laplacian(velocity(_).y,position),
               laplacian(velocity(_).z,position))   
  def velocityJacobian(position:Vector)
      = Matrix(gradient(velocity(X),position),
               gradient(velocity(Y),position),
               gradient(velocity(Z),position))
  
  def dvdt(position:Vector)={
    val v:Value = value(position)
    val vLaplacian:Vector = velocityLaplacian(position)
    val pressureGradient:Vector = gradient(pressure,position)
    val vJacobian:Matrix = velocityJacobian(position)
    val gravity = Vector(0,0,9.8)

   ((vLaplacian*v.viscosity minus pressureGradient)/v.density).
      add (gravity).
      minus (vJacobian * v.velocity)
  }

  def valueAfterTime(position:Vector,time:Double) 
       =  value(position).velocity.add(dvdt(position)*time)
}

case class Neighbours( 
    x1:Entry,x2:Entry,
    y1:Entry,y2:Entry,
    z1:Entry,z2:Entry)

class NavierStokes {
  def step(data:Data,timestep:Double):Data = data 
}


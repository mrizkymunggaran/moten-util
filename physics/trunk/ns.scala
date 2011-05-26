
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
  def sum = x+y+z
  def dot(v:Vector)=x*v.x+y*v.y+z*v.z 
}

case class Matrix(row1:Vector,row2:Vector,row3:Vector){
  def *(v:Vector) = Vector(row1.dot(v),row2.dot(v),row3.dot(v))
}

case class Value (
  velocity:Vector, pressure:Double, depth:Double, 
  density: Double, viscosity:Double, isWall:Boolean, 
  isBoundary:Map[Direction,Boolean]
)

case class Entry (position:Vector, value:Value)

trait Data {
  def pressure:Vector=>Double = {v:Vector=>value(v).pressure} 
  def velocity:Vector=>Vector = {v:Vector=>value(v).velocity}
  def value(vector:Vector):Value
  def gradient(f:Vector=>Double, position:Vector):Vector
  def gradient2nd(f:Vector=>Double, position:Vector):Vector
  def laplacian(f:Vector=>Double, position:Vector)
      = gradient2nd(f,position).sum
}

class NavierStokes {
 def step(data:Data,timestep:Double):Data = data 
}


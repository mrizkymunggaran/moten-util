import java.util.Date
import scala.collection.immutable._

case class IdentifierType(name:String) extends Ordered[IdentifierType] {
	def compare(that:IdentifierType):Int = 
		this.name.compareTo(that.name)
}

case class Identifier(typ:IdentifierType, value:String) extends Ordered[Identifier]{
	def compare(that:Identifier):Int = 
		if (this.typ.equals(that.typ))
			this.value.compareTo(that.value)
		else 
			this.typ.compare(that.typ)
}

case class TimedIdentifier(id:Identifier, time:Date) extends Ordered[TimedIdentifier]{
	def compare(that:TimedIdentifier):Int = 
		if (this.id.equals(that.id))
			this.time.compareTo(that.time)
		else
			this.id.compare(that.id)
}

class MetaData(entityId:long) 

case class MetaSet(set:Set[TimedIdentifier], meta: MetaData) 

case class MergeResult(a:MetaSet, b:MetaSet, c:MetaSet, d:Set[TimedIdentifier])


class System {

	implicit def toSet(a:MetaSet):Set[TimedIdentifier] = a.set
	implicit def toLong(d:java.util.Date):Long = d.getTime()

	def alpha(x:Set[TimedIdentifier], y:TimedIdentifier) = 
		x.filter(_.id.typ==y.id.typ).union(Set(y))

	def complement[T](x:Set[T], y:Set[T]):Set[T]= 
		x.filter(!y.contains(_))

	def time(x:Set[TimedIdentifier], y:TimedIdentifier) = {
		val a = x.find(_.id.typ>y.id.typ)
		a match {
			case t:Some[TimedIdentifier] => t.get
			case None => throw new RuntimeException("not found")
		}
	}

	def z(x:Set[TimedIdentifier], y:TimedIdentifier):Set[TimedIdentifier] = {
		val a = alpha(x,y)
		complement(x,a).union(Set(a.max))
	}	

	def z(x:Set[TimedIdentifier], y:Set[TimedIdentifier]):Set[TimedIdentifier] = {
		if (y.size==1) 
			z(x,y.head)
		else 
			z(z(x,y.head),y.tail)
	}
	
	def merge(a:MetaSet,b:MetaSet,c:MetaSet):MergeResult = {
		if (a.isEmpty) 
			MergeResult(a,b,c,Set())
		else if (b.isEmpty && c.isEmpty)
			MergeResult(a, b, c, Set())
		else if (c.isEmpty && a.size==1) {
			val a1=a.max
			if (a1.time >= time(b,a1).time)
				MergeResult(MetaSet(z(a,a.head),a.meta),
							MetaSet(Set(),b.meta),
							MetaSet(Set(),c.meta),
							Set())
			else
				MergeResult(MetaSet(Set(),a.meta), b, c, Set())
		}
		else		
			MergeResult(a, b, c, Set())
	}

}



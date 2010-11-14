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

case class MetaData 

case class MetaSet(set:Set[TimedIdentifier], meta: MetaData) 

case class MergeResult(a:MetaSet, b:MetaSet, c:MetaSet, d:Set[TimedIdentifier])


class System {

	implicit def toSet(a:MetaSet):Set[TimedIdentifier] = a.set
	implicit def toLong(d:java.util.Date):Long = d.getTime()

	def alpha(x:Set[TimedIdentifier], y:TimedIdentifier):Set[TimedIdentifier] = 
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

	def empty = MetaSet(emptySet,MetaData())
	def emptySet[T] = Set[T]()
	def >=(x:TimedIdentifier, y:Set[TimedIdentifier]):Boolean = {
		return x.time.getTime() >= time(y,x).time.getTime()
	}

	def merge(a1:TimedIdentifier, m:MetaData, b:MetaSet):MergeResult = {
		if (b.isEmpty)
			MergeResult(MetaSet(Set(a1),m), b, empty, emptySet)
		else if (>=(a1,b)) 
			MergeResult(MetaSet(z(b,a1),m),
						empty,
						empty,
						emptySet)
		else
			MergeResult(empty, b, empty, emptySet)
	}

	def merge(a1:TimedIdentifier, a2:TimedIdentifier, m:MetaData, b:MetaSet):MergeResult = {
		if (b.isEmpty)
			MergeResult(MetaSet(Set(a1,a2),m), empty, empty, emptySet)
		else if (>=(a1,b)) 
			MergeResult(MetaSet(z(z(b,a1),a2),m), empty, empty, emptySet)
		else 
			MergeResult(empty, b, empty, emptySet)	
	}

	def merge(a1:TimedIdentifier, a2:TimedIdentifier, m:MetaData, b:MetaSet, c:MetaSet):MergeResult = {
		if (a1.id == a2.id)
			merge(a1,m,b)
		else if (c.isEmpty)
			merge(a1,a2,m,b)
		else {
			val a:Set[TimedIdentifier] = Set(a1,a2)

			if (! >=(a1,b) && ! >=(a2,c))
				MergeResult(empty,b,c,emptySet)
			else if (>=(a1,b) && ! >=(a2,c))
				MergeResult(MetaSet(z(b,a1),m),empty,empty,emptySet)
			else if (a2.id == c.max.id)
				MergeResult(MetaSet(z(z(b,c),a),m),empty,empty,emptySet)
			else  {
					val aId = a.map(_.id)
					val cIntersection = c.set.filter(x => aId.contains(x.id))
					val cComplement = c.set.filter(x => !aId.contains(x.id))
					MergeResult(
						MetaSet(z(z(b,cIntersection),a),m),
						empty,
						MetaSet(cComplement,
						c.meta),
						emptySet)
				}
			}
		}
	}	

}



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

trait Domain[A] {
	def domain: Set[A]
}

trait EntityFunction extends Function[TimedIdentifier,Set[TimedIdentifier]] with Domain[TimedIdentifier]

class MyEntityFunction(map:Map[TimedIdentifier,Set[TimedIdentifier]]) extends EntityFunction {
	def apply(i:TimedIdentifier) = map.get(i) match {
		case si:Some[Set[TimedIdentifier]]  => si.get
		case _ => throw new NullPointerException 
	}
	def domain : Set[TimedIdentifier]= map.keySet 
	
}

class System(e:EntityFunction) {

	def add(a:SortedSet[TimedIdentifier]):System = {
		val a1 = a.max //assuming strongest is max
		val b = beta(e.domain,a1)				
		val c = a.map(gamma(e(a1),_)).flatten
		val domain2 = e.domain.filter(!b.contains(_)).filter(!c.contains(_))
		
		new System(e)
	}	
	private def beta(set:Set[TimedIdentifier],i:TimedIdentifier) = {
		set.filter( _.id == i.id).filter( _.time.compareTo(i.time)<0)
	}
	private def gamma(set:Set[TimedIdentifier],i:TimedIdentifier) = {
		set.filter( _.id.typ == i.id.typ).filter( _.time.compareTo(i.time)<0)
	}
}



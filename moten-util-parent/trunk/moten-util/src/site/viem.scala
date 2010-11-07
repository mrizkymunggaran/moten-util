import java.util.Date

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

class System(q:Set[TimedIdentifier], e:TimedIdentifier => TimedIdentifier) {
	def add(a:SortedSet[TimedIdentifier]):System {
		new System(q,e)
	}
}



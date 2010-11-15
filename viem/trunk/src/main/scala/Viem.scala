package org.moten.david.viem

/**
 * Volatile Identifier Entity Matching (VIEM) is an algorithm for matching identities
 * based on mostly unique identifiers that change over time and incorporates a correction 
 * mechanism for incorrectly reported identifiers.
 */
object `package` {}

import java.util.Date
import scala.collection.immutable._

/**
 * The type of an [[org.moten.david.viem.Identifier]]. For an example for a ship it might
 * be the MMSI number, so we might use IdentifierType("MMSI"). 
 * Has strict ordering, alphabetical at the moment.
 *
 * @param name the identitier type name
 */
case class IdentifierType(name:String) extends Ordered[IdentifierType] {
    def compare(that:IdentifierType):Int = 
        this.name.compareTo(that.name)
}

/**
 * Based on an [[org.moten.david.viem.IdentifierType]] and a value.
 * Has strict ordering based on IdentifierType ordering then value alphabetical
 * ordering.
 */
case class Identifier(typ:IdentifierType, value:String) extends Ordered[Identifier]{
    def compare(that:Identifier):Int = 
        if (this.typ.equals(that.typ))
            this.value.compareTo(that.value)
        else 
            this.typ.compare(that.typ)
}

/** 
 * An [[org.moten.david.viem.Identifier]] with a timestamp.
 */
case class TimedIdentifier(id:Identifier, time:Date) extends Ordered[TimedIdentifier]{
    def compare(that:TimedIdentifier):Int = 
        if (this.id.equals(that.id))
            this.time.compareTo(that.time)
        else
            this.id.compare(that.id)
}

case class MetaData(value:String) 
case class MetaSet(set:Set[TimedIdentifier], meta: MetaData) 

case class MergeResult(a:MetaSet, b:MetaSet, c:MetaSet, d:Set[TimedIdentifier])


class System {

    implicit def toSet(a:MetaSet):Set[TimedIdentifier] = a.set
    implicit def toLong(d:java.util.Date):Long = d.getTime()

    private def alpha(x:Set[TimedIdentifier], y:TimedIdentifier):Set[TimedIdentifier] = 
        x.filter(_.id.typ==y.id.typ).union(Set(y))

    private def complement[T](x:Set[T], y:Set[T]):Set[T]= 
        x.filter(!y.contains(_))

    private def typeMatch(x:Set[TimedIdentifier], y:TimedIdentifier):TimedIdentifier = {
        val a = x.find(_.id.typ == y.id.typ)
        a match {
            case t:Some[TimedIdentifier] => t.get
            case None => throw new RuntimeException("matching identifier type not found:" +y + " in " + x)
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

    def empty = MetaSet(emptySet,MetaData("empty"))
    def emptySet[T] = Set[T]()
    def >=(x:TimedIdentifier, y:Set[TimedIdentifier]):Boolean = {
        return x.time.getTime() >= typeMatch(y,x).time.getTime()
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
                    MetaSet(cComplement,c.meta),
                    emptySet)
            }
		}
    }   

}


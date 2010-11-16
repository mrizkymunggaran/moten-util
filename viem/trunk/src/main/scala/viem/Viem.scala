package viem

/**
 * Volatile Identifier Entity Matching (VIEM) is an algorithm for matching identities
 * based on mostly unique identifiers that change over time and incorporates a correction 
 * mechanism for incorrectly reported identifiers.
 */
object `package` {}

import java.util.Date
import scala.collection.immutable._
import viem.Merger._

/**
 * The type of an [[viem.Identifier]]. For an example for a ship it might
 * be the MMSI number, so we might use IdentifierType("MMSI"). 
 * Has strict ordering, reverse alphabetical at the moment.
 *
 * @param name the identitier type nameMergeResult(empty,MetaSet(z))
 */
case class IdentifierType(name: String) extends Ordered[IdentifierType] {
  def compare(that: IdentifierType): Int =
    that.name.compareTo(this.name)
}

/**
 * Based on an [[viem.IdentifierType]] and a value.
 * Has strict ordering based on IdentifierType ordering then value alphabetical
 * ordering.
 */
case class Identifier(typ: IdentifierType, value: String) extends Ordered[Identifier] {
  def compare(that: Identifier): Int =
    if (this.typ.equals(that.typ))
      this.value.compareTo(that.value)
    else
      this.typ.compare(that.typ)
}

/** 
 * An [[viem.Identifier]] with a timestamp.
 */
case class TimedIdentifier(id: Identifier, time: Date) extends Ordered[TimedIdentifier] {
  def compare(that: TimedIdentifier): Int =
    //compare using only type and time (not identifier value)
    if (this.id.typ.equals(that.id.typ))
      this.time.compareTo(that.time)
    else
      this.id.compare(that.id)
}

/**
 * Ancillary (meta) data with a value. Would be used for an entityId for example.
 *
 */
trait MetaData

/**
 * Container for a set of [[viem.TimedIdentifier]] associated with some [[viem.MetaData]].
 */
case class MetaSet(set: Set[TimedIdentifier], meta: MetaData)

/**
 * The result of the merge of [[viem.MetaSet]] with primary and secondary matches (b and c)
 * and the effect of stripping if the new MetaSet was rejected.
 */
case class MergeResult(a: MetaSet, b: MetaSet, c: MetaSet, d: Set[TimedIdentifier])

/**
 * Validates the merging of two entities based on their [[viem.MetaData]].
 */
abstract trait MergeValidator {
    def mergeIsValid(a:MetaData, b:MetaData):Boolean
}

/**
 * Utility class for performing merges of [[scala.collection.immutable.Set]] of [[viem.TimedIdentifier]].
 */
class Merger(mergeValidator:MergeValidator) {

  implicit def toSet(a: MetaSet): Set[TimedIdentifier] = a.set
  implicit def toLong(d: java.util.Date): Long = d.getTime()


  def alpha(x: Set[TimedIdentifier], y: TimedIdentifier): Set[TimedIdentifier] = {
    val set = x.filter(_.id.typ == y.id.typ)
    set.union(Set(y))
  }

  def complement[T](x: Set[T], y: Set[T]): Set[T] =
    x.filter(!y.contains(_))

  def typeMatch(x: Set[TimedIdentifier], y: TimedIdentifier): TimedIdentifier = {
    val a = x.find(_.id.typ == y.id.typ)
    a match {
      case t: Some[TimedIdentifier] => t.get
      case None => throw new RuntimeException("matching identifier type not found:" + y + " in " + x)
    }
  }

  def z(x: Set[TimedIdentifier], y: TimedIdentifier): Set[TimedIdentifier] = {
    val a = alpha(x, y)
    complement(x, a).union(Set(a.max))
  }

  def z(x: Set[TimedIdentifier], y: Set[TimedIdentifier]): Set[TimedIdentifier] = {
    if (y.size==0)
        x
    else if (y.size == 1)
      z(x, y.head)
    else
      z(z(x, y.head), y.tail)
  }
  
 
  
  def >=(x: TimedIdentifier, y: Set[TimedIdentifier]): Boolean = {
    return x.time.getTime() >= typeMatch(y, x).time.getTime()
  }

  def merge(a1: TimedIdentifier, m: MetaData, b: MetaSet): MergeResult = {
    if (b.isEmpty)
      MergeResult(MetaSet(Set(a1), m), b, empty, emptySet)
    else if (>=(a1, b))
      MergeResult(
        MetaSet(z(b, a1), m),
        empty,
        empty,
        emptySet)
    else
      MergeResult(empty, b, empty, emptySet)
  }

  def merge(a1: TimedIdentifier, a2: TimedIdentifier, m: MetaData, b: MetaSet): MergeResult = {
    if (b.isEmpty)
      MergeResult(MetaSet(Set(a1, a2), m), empty, empty, emptySet)
    else if (>=(a1, b))
      MergeResult(MetaSet(z(z(b, a1), a2), m), empty, empty, emptySet)
    else
      MergeResult(empty, MetaSet(z(b,a2),b.meta), empty, emptySet)
  }

  def merge(a1: TimedIdentifier, a2: TimedIdentifier, m: MetaData, b: MetaSet, c: MetaSet): MergeResult = {
    if (a1.time != a2.time) 
        throw new RuntimeException("a1 and a2 must have the same time")
    if (!b.isEmpty && !b.map(_.id).contains(a1.id)) 
        throw new RuntimeException("a1 id must be in b");
    if (!c.isEmpty && !c.map(_.id).contains(a2.id)) 
        throw new RuntimeException("a2 id must be in c if c is non-empty");
    if (c.isEmpty && !b.isEmpty && !b.map(_.id).contains(a1.id)) 
        throw new RuntimeException("a2 id must be in b if c is empty")
    if (a1.id == a2.id)
      merge(a1, m, b)
    else if (c.isEmpty)
      merge(a1, a2, m, b)
    else {
      val a: Set[TimedIdentifier] = Set(a1, a2)

      if (! >=(a1, b) && ! >=(a2, c))
        if (mergeValidator.mergeIsValid(b.meta , c.meta))
            //TODO merge b and c
            MergeResult(empty, b, c, emptySet)
        else
            MergeResult(empty, b, c, emptySet)
      else if (>=(a1, b) && ! >=(a2, c))
        MergeResult(MetaSet(z(b, a1), m), empty, empty, emptySet)
      else if (a2.id == c.max.id)
        MergeResult(MetaSet(z(z(b, c), a), m), empty, empty, emptySet)
      else {
        val aTypes = a.map(_.id.typ)
        val cIntersection = c.set.filter(x => aTypes.contains(x.id.typ))
        val cComplement = c.set.filter(x => !aTypes.contains(x.id.typ))
        val c2 = if (cComplement.isEmpty) empty else MetaSet(cComplement,c.meta)
        MergeResult(
          MetaSet(z(z(b, cIntersection), a), m),
          empty,
          c2,
          Merger.emptySet)
      }
    }
  }

}

object Merger {
    
  case class EmptyMetaData() extends MetaData
    
  val emptyMetaData = EmptyMetaData()

  val empty = MetaSet(emptySet, emptyMetaData)
  
  def emptySet[T] = Set[T]()
}
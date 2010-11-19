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
 * be the MMSI number, so we might use [[viem.IdentifierType]]("MMSI"). 
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
 * Has strict ordering based on [[viem.IdentifierType]] ordering then value alphabetical
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
 * The result of a merge. This trait is sealed so that all its 
 * implementations must come from this class.
 * @author dxm
 *
 */
sealed trait Result

/**
 * Invalid merge result.
 * 
 * @param meta is the meta data of the set that provoked the invalid merge. 
 * For ''a'' against primary match ''b'' it will be ''b.meta''. For ''a'' 
 * against secondary match ''c'' it will be ''c.meta''. For ''b'' against 
 * ''c'' it will be the meta of the weaker identifier set, that is c.meta. 
 *
 */
case class InvalidMerge(meta: MetaData) extends Result

/**
 * The result of the merge of [[viem.MetaSet]] with primary and secondary matches (b and c).
 */
case class MergeResult(a: MetaSet, b: MetaSet, c: MetaSet) extends Result

/**
 * Validates the merging of two entities based on their [[viem.MetaData]].
 */
abstract trait MergeValidator {
  def mergeIsValid(a: MetaData, b: MetaData): Boolean
}

/**
 * Utility class for performing merges of [[scala.collection.immutable.Set]] of [[viem.TimedIdentifier]].
 */
class Merger(mergeValidator: MergeValidator) {

  implicit def toSet(a: MetaSet): Set[TimedIdentifier] = a.set

  /**
   * Returns ''y'' and the member of set ''x'' that matches the [[viem.IdentifierType]] of
   * ''y'' as a two element set if and only the type matching item in ''x'' was found, or a one element 
   * set if the item in ''x'' with matching [[viem.IdentifierType]] was not found.
   * @param x
   * @param y
   * @return
   */
  def alpha(x: Set[TimedIdentifier], y: TimedIdentifier): Set[TimedIdentifier] = {
    val set = x.filter(_.id.typ == y.id.typ)
    set.union(Set(y))
  }

  /**
   * Returns the complement of two sets of generic type ''T''. 
   * @param x
   * @param y
   * @return
   */
  def complement[T](x: Set[T], y: Set[T]): Set[T] =
    x.filter(!y.contains(_))

  /**
   * Returns the (first) item in x that has the same [[viem.IdentifierType]] as y.
   * Throws a [[java.lang.RuntimeException]] if the identifier type of x not found in y.
   * @param x
   * @param y
   * @return
   */
  def typeMatch(x: Set[TimedIdentifier], y: TimedIdentifier): TimedIdentifier = {
    val a = x.find(_.id.typ == y.id.typ)
    a match {
      case t: Some[TimedIdentifier] => t.get
      case None => error("matching identifier type not found:" + y + " in " + x)
    }
  }

  /**
   * Combines a set of [[viem.TimedIdentifier]] with another [[viem.TimedIdentifier]]. Ensures that
   * if the identifier type of ''y'' exists in x that the returned set only contains
   * the latest identifier from x and y of that type unioned with all other identifiers in x.
   * @param x
   * @param y
   * @return
   */
  def z(x: Set[TimedIdentifier], y: TimedIdentifier): Set[TimedIdentifier] = {
    val a = alpha(x, y)
    complement(x, a).union(Set(a.max))
  }

  /**
   * Combines a set of [[viem.TimedIdentifier]] with another set  of [[viem.TimedIdentifier]].
   * Ensures that if the identifier types in ''y'' exist in x that the returned set only contains
   * the latest identifiers from x and y of that type unioned with all other identifiers in x 
   * that don't have an identifier type in y.
   * @param x
   * @param y
   * @return
   */
  def z(x: Set[TimedIdentifier], y: Set[TimedIdentifier]): Set[TimedIdentifier] = {
    if (y.size == 0)
      x
    else if (y.size == 1)
      z(x, y.head)
    else
      z(z(x, y.head), y.tail)
  }

  /**
   * Returns true if and only if the time of x is greater or equal to the time of 
   * the identifier in y with the same identifier type. If the identifier type of x
   * does not exist in y then a [[java.lang.RuntimeException]] is thrown.
   * @param x
   * @param y
   * @return
   */
  def >=(x: TimedIdentifier, y: Set[TimedIdentifier]): Boolean = {
    return x.time.getTime() >= typeMatch(y, x).time.getTime()
  }

  /**
   * Returns the result of merging ''a1'' with associated metadata ''m'' with 
   * the set ''b''. ''b'' is expected to have an identifier with type and value of a1.
   * @param a1
   * @param m
   * @param b
   * @return
   */
  def merge(a1: TimedIdentifier, m: MetaData, b: MetaSet): MergeResult = {
    if (b.isEmpty)
      MergeResult(MetaSet(Set(a1), m), b, empty)
    else if (>=(a1, b))
      MergeResult(
        MetaSet(z(b, a1), m),
        empty,
        empty)
    else
      MergeResult(empty, b, empty)
  }

  /**
   * Returns the result of merging timed identifiers a1 and a2 both with associated
   * metadata m with the set b. Both a1 and a2 must be present in b (possibly with
   * different times though).
   * @param a1
   * @param a2
   * @param m
   * @param b
   * @return
   */
  def merge(a1: TimedIdentifier, a2: TimedIdentifier, m: MetaData, b: MetaSet): MergeResult = {
    if (b.isEmpty)
      MergeResult(MetaSet(Set(a1, a2), m), empty, empty)
    else if (>=(a1, b))
      MergeResult(MetaSet(z(z(b, a1), a2), m), empty, empty)
    else
      MergeResult(empty, MetaSet(z(b, a2), b.meta), empty)
  }

  /**
   * Returns the result of the merge of a1 and a2 with associated metadata m,
   * with the sets b and c. a1 must be in b and a2 must be in c (although 
   * possibly with different times).
   * @param a1
   * @param a2
   * @param m
   * @param b
   * @param c
   * @return
   */
  def merge(a1: TimedIdentifier, a2: TimedIdentifier, m: MetaData, b: MetaSet, c: MetaSet): Result = {

    //do some precondition checks on the inputs
    assert(a1.time == a2.time, "a1 and a2 must have the same time because they came from the same fix set")
    assert(b.isEmpty || b.map(_.id).contains(a1.id), "a1 id must be in b if b is non-empty")
    assert(c.isEmpty || c.map(_.id).contains(a2.id), "a2 id must be in c if c is non-empty")
    assert(!(c.isEmpty && !b.isEmpty && !b.map(_.id).contains(a1.id)), "a2 id must be in b if c is empty")
    assert(b.map(_.id.typ).size == b.size, "b must not have more than one identifier of any type")
    assert(c.map(_.id.typ).size == c.size, "c must not have more than one identifier of any type")
    assert(b.map(_.id).intersect(c.map(_.id)).size == 0, "b and c cannot have an identifier in common")

    if (!b.isEmpty && !mergeValidator.mergeIsValid(m, b.meta))
      return InvalidMerge(b.meta)
    else if (a1.id == a2.id)
      merge(a1, m, b)
    else if (c isEmpty)
      merge(a1, a2, m, b)
    else {
      val a: Set[TimedIdentifier] = Set(a1, a2)

      if (!(>=(a1, b)) && !(>=(a2, c)))
        if (!mergeValidator.mergeIsValid(m, c.meta))
          return InvalidMerge(c.meta)
        else if (mergeValidator.mergeIsValid(b.meta, c.meta)) {
          //if b and c have conflicting identifiers that both have later 
          //timestamps than a1 (or a2) then don't merge (no validity problem though)

          //calculate common identifier types with different identifier values in b
          val b2 = b.filter(t => c.map(x => x.id.typ).contains(t.id.typ) && !c.map(x => x.id).contains(t.id))
          //calculate common identifier types with different identifier values in c
          val c2 = c.filter(t => b.map(x => x.id.typ).contains(t.id.typ) && !b.map(x => x.id).contains(t.id))
          if (!b2.isEmpty && b2.map(_.time.getTime()).max > a1.time.getTime())
            //don't merge
            return MergeResult(empty, b, c)
          else if (!c2.isEmpty && c2.map(_.time.getTime()).max > a1.time.getTime())
            return MergeResult(empty, b, c)
          else
            return MergeResult(empty, MetaSet(z(b, c), b.meta), empty)
        } else
          return InvalidMerge(c.meta)
      else if (>=(a1, b) && !(>=(a2, c)))
        MergeResult(MetaSet(z(b, a1), m), empty, empty)
      else if (a2.id == c.max.id)
        MergeResult(MetaSet(z(z(b, c), a), m), empty, empty)
      else {
        val aTypes = a.map(_.id.typ)
        val cIntersection = c.set.filter(x => aTypes.contains(x.id.typ))
        val cComplement = c.set.filter(x => !aTypes.contains(x.id.typ))
        val c2 = if (cComplement.isEmpty) empty else MetaSet(cComplement, c.meta)
        MergeResult(
          MetaSet(z(z(b, cIntersection), a), m),
          empty,
          c2)
      }
    }
  }

  /**
   *Returns the result of merging the identifiers in a (max 2) with ''b'' and ''c''.
   * @param a
   * @param b
   * @param c
   * @return
   */
  def mergePair(a: MetaSet, b: MetaSet, c: MetaSet): Result = {
    assert(a.size <= 2, "a must have a size of 2 or less")
    if (a.isEmpty) MergeResult(empty, b, c)
    else if (a.size == 1) merge(a.max, a.max, a.meta, b, c)
    else merge(a.max, a.min, a.meta, b, c)
  }

}

/**
 * Utility methods for merging.
 * @author dxm
 *
 */
object Merger {

  case class EmptyMetaData() extends MetaData

  val emptyMetaData = EmptyMetaData()

  val empty = MetaSet(Set(), emptyMetaData)

}
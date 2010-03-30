package moten.david.ete;

import java.util.Set;

/**
 * Provides broader functionality that doesn't naturally sit with one of the
 * other interfaces such as Entity or Identifier or Rank.
 * 
 * @author dave
 * 
 */
public interface Engine {

	/**
	 * Return true if and only if the fix has already been reported to this
	 * engine.
	 * 
	 * @param fix
	 * @return
	 */
	boolean hasFixAlready(Fix fix);

	/**
	 * Factory method to create an Entity with the given identifiers.
	 * 
	 * @param identifiers
	 * @return
	 */
	Entity createEntity(Set<Identifier> identifiers);

	/**
	 * Will try to match on the identifiers in descending rank order
	 * (IdentiferType is a Comparable). Once a match is found the other
	 * identifiers are ignored.
	 * 
	 * @param identifiers
	 * @return
	 */
	Entity findEntity(Set<Identifier> identifiers);

}

package moten.david.ete;

import java.util.Enumeration;
import java.util.SortedSet;

/**
 * Provides broader functionality that doesn't naturally sit with one of the
 * other interfaces as non static methods on classes such as Entity or
 * Identifier or Rank.
 * 
 * @author dave
 */
public interface Engine {

	/**
	 * Factory method to create an Entity with the given identifiers.
	 * 
	 * @param identifiers
	 * @return
	 */
	Entity createEntity(SortedSet<? extends Identifier> identifiers);

	/**
	 * Remove an entity and all it's associated data (e.g. fixes).
	 * 
	 * @param entity
	 */
	void removeEntity(Entity entity);

	/**
	 * Will try to match on the identifiers in descending rank order
	 * (IdentiferType is a Comparable). Once a match is found the other
	 * identifiers are ignored.
	 * 
	 * @param identifiers
	 * @return
	 */
	Entity findEntity(SortedSet<? extends Identifier> identifiers);

	/**
	 * Returns an enumeration of all the entities recorded in the engine.
	 * 
	 * @return
	 */
	Enumeration<Entity> getEntities();

}

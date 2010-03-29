package moten.david.ete;

import java.util.Set;

public interface Engine {
	boolean hasFixAlready(Fix fix);

	Entity createEntity(Set<Identifier> identifiers);

	/**
	 * Will try to match on the identifiers in order of descending rank. Once a
	 * match is find the other identifiers are ignored.
	 * 
	 * @param identifiers
	 * @return
	 */
	Entity findEntity(Set<Identifier> identifiers);

}

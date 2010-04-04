package moten.david.ete;

import java.util.SortedSet;

public interface Identifiers {

	void add(Identifier identifier);

	void remove(Identifier identifier);

	/**
	 * Returns an unmodifiable set of the identifiers in descending order of
	 * identifier type (strongest first).
	 * 
	 * @return
	 */
	SortedSet<? extends Identifier> set();
}

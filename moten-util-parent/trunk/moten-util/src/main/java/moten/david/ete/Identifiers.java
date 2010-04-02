package moten.david.ete;

import java.util.SortedSet;

public interface Identifiers {

	void add(Identifier identifier);

	void remove(Identifier identifier);

	/**
	 * Returns an unmodifiable set of the identifiers.
	 * 
	 * @return
	 */
	SortedSet<Identifier> set();
}

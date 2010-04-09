package moten.david.imatch;

import java.util.Comparator;

import com.google.common.collect.ImmutableSet;

public interface Datastore {

	ImmutableSet<Identifier> identifiers();

	ImmutableSet<IdentifierSet> identifierSets();

	IdentifierSet beta(IdentifierSet set, Identifier identifier);

	IdentifierSet r(IdentifierSet set);

	IdentifierSet alpha(Identifier identifier);

	double time(IdentifierSet set);

	IdentifierSet merge(IdentifierSet identifierSet, Identifier identifier);

	Datastore add(IdentifierSet set, double time);

	// intermediate functions

	IdentifierSet nms(IdentifierSet x, IdentifierSet y);

	IdentifierSet pm(IdentifierSet x);

	IdentifierType t(Identifier x);

	/**
	 * > comparator
	 * 
	 * @return
	 */
	Comparator<IdentifierType> strengthOrdering();

	/**
	 * >> comparator
	 * 
	 * @return
	 */
	Comparator<IdentifierType> strictOrdering();
}

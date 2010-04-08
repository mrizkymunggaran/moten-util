package moten.david.imatch;

import com.google.common.collect.ImmutableSet;

public interface Datastore {

	ImmutableSet<Identifier> identifiers();

	IdentifierSet beta(IdentifierSet set, Identifier identifier);

	IdentifierSet r(IdentifierSet set);

	IdentifierSet alpha(Identifier identifier);

	double time(IdentifierSet set);

	IdentifierSet merge(IdentifierSet identifierSet, Identifier identifier);

	Datastore add(IdentifierSet set, double time);

	// intermediate functions

	double dmax(IdentifierSet x);

	IdentifierSet nms(IdentifierSet x, IdentifierSet y);

	IdentifierSet pm(IdentifierSet x);

	IdentifierType t(Identifier x);

	double d(IdentifierType t);

}

package moten.david.imatch;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public interface IdentifierSet {

	boolean contains(Identifier identifier);

	IdentifierSet union(IdentifierSet set);

	IdentifierSet complement(IdentifierSet set);

	boolean equals(Object o);

	boolean isEmpty();

	ImmutableSet<Identifier> set();

	IdentifierSet filter(Predicate<Identifier> predicate);

	IdentifierSet add(Identifier identifier);

}

package moten.david.imatch;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public interface IdentifierSet {

	boolean contains(Identifier identifier);

	IdentifierSet union(IdentifierSet set);

	IdentifierSet complement(IdentifierSet set);

	boolean equals(Object o);

	boolean isEmpty();

	ImmutableList<Identifier> list();

	IdentifierSet filter(Predicate predicate);

	IdentifierSet add(Identifier identifier);

}

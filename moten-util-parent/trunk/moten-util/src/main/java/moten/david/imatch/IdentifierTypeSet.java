package moten.david.imatch;

import com.google.common.collect.ImmutableSet;

public interface IdentifierTypeSet {
	boolean contains(IdentifierType type);

	ImmutableSet<IdentifierType> set();

	IdentifierTypeSet add(IdentifierType identifierType);
}

package moten.david.ete.memory;

import java.util.SortedSet;

import moten.david.ete.Identifier;

public interface MyIdentifiersFactory {
	MyIdentifiers create(SortedSet<? extends Identifier> identifiers,
			MyEntity entity);
}

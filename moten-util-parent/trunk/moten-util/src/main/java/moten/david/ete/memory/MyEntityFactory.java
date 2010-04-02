package moten.david.ete.memory;

import java.util.SortedSet;

import moten.david.ete.Identifier;

public interface MyEntityFactory {
	MyEntity create(SortedSet<Identifier> ids);
}

package moten.david.ete.memory;

import java.util.SortedSet;

import moten.david.ete.Entity;
import moten.david.ete.Identifier;

public interface EntityLookup {

	public Entity findEntity(SortedSet<Identifier> identifiers);

}

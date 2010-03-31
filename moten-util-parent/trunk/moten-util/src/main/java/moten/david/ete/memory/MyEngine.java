package moten.david.ete.memory;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Identifier;
import moten.david.ete.Util;

public class MyEngine implements Engine {

	private final Set<Entity> entities = new HashSet<Entity>();

	@Override
	public Entity createEntity(Set<Identifier> identifiers) {
		MyEntity entity = new MyEntity(new TreeSet<Identifier>(identifiers));
		entities.add(entity);
		return entity;
	}

	@Override
	public Entity findEntity(Set<Identifier> identifiers) {
		SortedSet<Identifier> sorted = new TreeSet<Identifier>(identifiers);
		for (Entity entity : entities) {
			if (Util.haveCommonIdentifier(entity.getIdentifiers(), sorted))
				return entity;
		}
		return null;
	}

}

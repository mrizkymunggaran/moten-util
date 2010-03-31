package moten.david.ete.memory;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;

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
		// TOOD write this
		return null;
	}

	@Override
	public boolean hasFixAlready(Fix fix) {
		// TODO Auto-generated method stub
		return false;
	}

}

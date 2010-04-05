package moten.david.ete.memory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;
import moten.david.util.collections.CollectionsUtil;

import com.google.inject.Inject;

public class MyEngine implements Engine {

	private final Set<Entity> entities = Collections
			.synchronizedSet(new HashSet<Entity>());

	private final MyEntityFactory entityFactory;

	private final EntityLookup entityLookup;

	@Inject
	public MyEngine(MyEntityFactory entityFactory, EntityLookup entityLookup) {
		this.entityFactory = entityFactory;
		this.entityLookup = entityLookup;
	}

	@Override
	public Entity createEntity(SortedSet<? extends Identifier> ids) {
		synchronized (entities) {
			final MyEntity entity = entityFactory.create(ids);
			entities.add(entity);
			return entity;
		}
	}

	@Override
	public Entity findEntity(SortedSet<? extends Identifier> identifiers) {
		synchronized (entities) {
			return entityLookup.findEntity(identifiers);
		}
	}

	@Override
	public void removeEntity(Entity entity) {
		synchronized (entities) {
			if (!entities.remove(entity))
				throw new RuntimeException("Could not remove entity!");
		}
	}

	@Override
	public Enumeration<Entity> getEntities() {
		synchronized (entities) {
			return CollectionsUtil.toEnumeration(entities.iterator());
		}
	}

	public Enumeration<MyFix> getLatestFixes() {
		List<MyFix> list = new ArrayList<MyFix>();
		for (Entity e : CollectionsUtil.toList(getEntities())) {
			list.add((MyFix) e.getLatestFix());
		}
		return CollectionsUtil.toEnumeration(list.iterator());
	}

	/**
	 * Saves all fixes to an OutputStream
	 * 
	 * @param os
	 */
	public long saveFixes(OutputStream os) {
		try {
			long count = 0;
			ObjectOutputStream oos = new ObjectOutputStream(os);
			Enumeration<Entity> en = getEntities();
			while (en.hasMoreElements()) {
				MyEntity entity = (MyEntity) en.nextElement();
				for (Fix fix : entity.getFixes()) {
					oos.writeObject(fix);
					count++;
				}
			}
			oos.close();
			return count;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

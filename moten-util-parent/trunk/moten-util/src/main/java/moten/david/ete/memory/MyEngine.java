package moten.david.ete.memory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Identifier;
import moten.david.ete.Util;
import moten.david.util.collections.CollectionsUtil;

public class MyEngine implements Engine {

	private final FixTrimmer fixTrimmer;

	public MyEngine() {
		this.fixTrimmer = new FixTrimmer(this, 100000);
	}

	private final Set<Entity> entities = Collections
			.synchronizedSet(new HashSet<Entity>());

	@Override
	public Entity createEntity(SortedSet<Identifier> identifiers) {
		synchronized (entities) {
			MyEntity entity = new MyEntity(identifiers);
			entities.add(entity);
			entity.addListener(fixTrimmer);
			return entity;
		}
	}

	@Override
	public Entity findEntity(SortedSet<Identifier> identifiers) {
		synchronized (entities) {
			for (Entity entity : entities) {
				if (Util.haveCommonIdentifier(entity.getIdentifiers(),
						identifiers))
					return entity;
			}
			return null;
		}
	}

	@Override
	public void removeEntity(Entity entity) {
		synchronized (entities) {
			entities.remove(entity);
			fixTrimmer.entityRemoved(entity);
		}
	}

	@Override
	public Enumeration<Entity> getEntities() {
		synchronized (entities) {
			return CollectionsUtil.toEnumeration(entities.iterator());
		}
	}

	public Enumeration<MyFix> getLatestFixes() {
		return new Enumeration<MyFix>() {

			private Iterator<MyFix> iterator;

			{
				List<MyFix> list = new ArrayList<MyFix>();
				for (Entity e : CollectionsUtil.toList(getEntities())) {
					list.add((MyFix) e.getLatestFix());
				}
				iterator = list.iterator();
			}

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public MyFix nextElement() {
				return iterator.next();
			}

		};
	}

	/**
	 * Saves all fixes to an OutputStream
	 * 
	 * @param os
	 */
	public void saveFixes(OutputStream os) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			Enumeration<Entity> en = getEntities();
			while (en.hasMoreElements()) {
				MyEntity entity = (MyEntity) en.nextElement();
				Enumeration<MyFix> enFixes = entity.getFixes();
				while (enFixes.hasMoreElements()) {
					MyFix fix = enFixes.nextElement();
					oos.writeObject(fix);
				}
			}
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

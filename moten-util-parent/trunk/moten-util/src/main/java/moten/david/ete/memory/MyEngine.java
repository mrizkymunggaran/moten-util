package moten.david.ete.memory;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Identifier;
import moten.david.ete.Util;
import moten.david.util.collections.CollectionsUtil;

public class MyEngine implements Engine {

    private final Set<Entity> entities = Collections
            .synchronizedSet(new HashSet<Entity>());

    @Override
    public Entity createEntity(SortedSet<Identifier> identifiers) {
        synchronized (entities) {
            MyEntity entity = new MyEntity(identifiers);
            entities.add(entity);
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
        }
    }

    @Override
    public Enumeration<Entity> getEntities() {
        synchronized (entities) {
            return CollectionsUtil.toEnumeration(entities.iterator());
        }
    }
}

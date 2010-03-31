package moten.david.ete.memory;

import java.util.HashSet;
import java.util.Set;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;

public class MyEngine implements Engine {

    private final Set<Entity> entities = new HashSet<Entity>();

    @Override
    public Entity createEntity(Set<Identifier> identifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity findEntity(Set<Identifier> identifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasFixAlready(Fix fix) {
        // TODO Auto-generated method stub
        return false;
    }

}

package moten.david.imatch.memory;

import java.util.Map;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.Merger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;

public class DatastoreImmutable implements Datastore {

    private final ImmutableMap<Identifier, IdentifierSet> map;
    private final ImmutableMap<IdentifierSet, Double> times;
    private final Merger merger;

    public DatastoreImmutable(Map<Identifier, IdentifierSet> map,
            Map<IdentifierSet, Double> times, Merger merger) {
        this.merger = merger;
        this.map = ImmutableMap.copyOf(map);
        this.times = ImmutableMap.copyOf(times);
    }

    @Override
    public IdentifierSet alpha(Identifier identifier) {
        IdentifierSet value = map.get(identifier);
        if (value == null)
            return MyIdentifierSet.EMPTY_SET;
        else
            return value;
    }

    @Override
    public double time(IdentifierSet set) {
        return times.get(set);
    }

    @Override
    public ImmutableSet<Identifier> identifiers() {
        return map.keySet();
    }

    @Override
    public Datastore add(IdentifierSet set, double time) {
        Builder<Identifier, IdentifierSet> mapBuilder = ImmutableMap.builder();
        Builder<IdentifierSet, Double> timesBuilder = ImmutableMap.builder();
        for (Identifier i : map.keySet()) {
            IdentifierSet s = merger.merge(set, i);
            mapBuilder.put(i, s);
            if (set.contains(i))
                timesBuilder.put(s, time);
        }
        return new DatastoreImmutable(mapBuilder.build(), timesBuilder.build(),
                merger);
    }
}

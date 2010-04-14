package moten.david.imatch.memory;

import java.util.Map;
import java.util.Set;

import moten.david.imatch.IdentifierSet;

public interface DatastoreImmutableFactory {
    DatastoreImmutable create(Set<IdentifierSet> sets,
            Map<IdentifierSet, Double> times);
}

package moten.david.imatch.memory;

import java.util.Map;
import java.util.Set;

import moten.david.imatch.IdentifierSet;

public interface DatastoreImmutable2Factory {
    DatastoreImmutable2 create(Set<IdentifierSet> sets,
            Map<IdentifierSet, Double> times);
}

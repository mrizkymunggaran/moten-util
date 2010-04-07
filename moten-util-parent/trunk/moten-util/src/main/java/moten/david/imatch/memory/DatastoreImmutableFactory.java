package moten.david.imatch.memory;

import java.util.Map;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;

public interface DatastoreImmutableFactory {
    DatastoreImmutable create(Map<Identifier, IdentifierSet> map,
            Map<IdentifierSet, Double> times);
}

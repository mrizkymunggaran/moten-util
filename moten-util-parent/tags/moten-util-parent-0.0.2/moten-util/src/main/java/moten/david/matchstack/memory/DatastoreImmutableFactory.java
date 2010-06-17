package moten.david.matchstack.memory;

import java.util.Set;

import moten.david.matchstack.TimedIdentifier;

public interface DatastoreImmutableFactory {
    DatastoreImmutable create(Set<Set<TimedIdentifier>> sets);
}

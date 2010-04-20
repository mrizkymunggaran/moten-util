package moten.david.imatch.memory;

import java.util.Set;

import moten.david.imatch.TimedIdentifier;

public interface DatastoreImmutableFactory {
    DatastoreImmutable create(Set<Set<TimedIdentifier>> sets);
}

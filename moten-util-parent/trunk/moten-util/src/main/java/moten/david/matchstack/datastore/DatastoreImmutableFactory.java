package moten.david.matchstack.datastore;

import java.util.Set;

import moten.david.matchstack.types.TimedIdentifier;

/**
 * Factory for creating instances of {@link DatastoreImmutable}.
 * 
 * @author dave
 * 
 */
public interface DatastoreImmutableFactory {
    /**
     * Creates an instance of {@link DatastoreImmutable}
     * 
     * @param sets
     * @return
     */
    DatastoreImmutable create(Set<Set<TimedIdentifier>> sets);
}

package moten.david.ets.server;

/**
 * Represents the entities for which fixes are being supplied.
 * 
 * @author dxm
 */
public interface Entities {

    /**
     * Adds fixes to the entities and merges/changes entity identifiers as
     * determined by the merge engine.
     * 
     * @param fix
     */
    void add(Iterable<MyFix> fix);

    /**
     * Clear all entities and associated data.
     */
    void clearAll();

}

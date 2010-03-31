package moten.david.ete;

/**
 * Identifies an entity. The Comparable interface will be used to sort
 * Identifiers in ascending order of Identifier Type strength.
 * 
 * @author dave
 */
public interface Identifier extends Comparable<Identifier> {

    /**
     * Returns the type of the identifier.
     * 
     * @return
     */
    IdentifierType getIdentifierType();

}

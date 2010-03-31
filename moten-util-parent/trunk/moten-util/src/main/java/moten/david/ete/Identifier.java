package moten.david.ete;

/**
 * Identifies an entity. The Comparable interface will be used to sort
 * Identifiers in ascending order of Identifier Type strength.
 * 
 * @author dave
 */
public abstract class Identifier implements Comparable<Identifier> {

    /**
     * Returns the type of the identifier.
     * 
     * @return
     */
    public abstract IdentifierType getIdentifierType();

    /*
     * Use the IdentifierType to do the comparison. (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Identifier id) {
        return this.getIdentifierType().compareTo(id.getIdentifierType());
    }
}

package moten.david.matchstack;

/**
 * A generic identifier which has a type.
 * 
 * @author dave
 * 
 */
public interface Identifier {
    /**
     * Gets the type of the identifier.
     * 
     * @return
     */
    IdentifierType getIdentifierType();
}

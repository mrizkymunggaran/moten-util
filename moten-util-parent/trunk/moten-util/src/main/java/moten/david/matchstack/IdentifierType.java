package moten.david.matchstack;

/**
 * Classifies an identifier.
 * 
 * @author dave
 * 
 */
public interface IdentifierType {

    /**
     * Returns the strength of an identifier. The higher the number, the
     * stronger the identifier. For example IMO Number is a stronger identifier
     * than Name for ships because Name is volatile and can change as owners
     * change whereas IMO Number stays with the ship for its life. Note that
     * different identifier types may have the same strength.
     * 
     * @return
     */
    double getStrength();

}

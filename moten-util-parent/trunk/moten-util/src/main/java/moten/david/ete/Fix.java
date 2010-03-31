package moten.david.ete;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;

/**
 * A reported position of an entity with the identifiers given.
 * 
 * @author dave
 */
public interface Fix extends Comparable<Fix> {
    /**
     * Entity identifiers.
     * 
     * @return
     */
    SortedSet<Identifier> getIdentifiers();

    /**
     * The position of the entity.
     * 
     * @return
     */
    Position getPosition();

    /**
     * The time that the position occurred.
     * 
     * @return
     */
    Calendar getTime();

    /**
     * Any miscellaneous properties of the fix.
     * 
     * @return
     */
    Map<String, String> getProperties();
}

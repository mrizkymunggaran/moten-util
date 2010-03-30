package moten.david.ete;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * A reported position of an entity with the identifiers given.
 * 
 * @author dave
 * 
 */
public interface Fix {
	/**
	 * Entity identifiers.
	 * 
	 * @return
	 */
	Set<Identifier> getIdentifiers();

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
	 * The source of the fix.
	 * 
	 * @return
	 */
	Source getSource();

	/**
	 * Any miscellaneous properties of the fix.
	 * 
	 * @return
	 */
	Map<String, String> getProperties();

	/**
	 * The type of entity referred to by this fix.
	 * 
	 * @return
	 */
	EntityType getType();
}

package moten.david.ete;

import java.util.SortedSet;

public interface Entity {

	/**
	 * Sorted in ascending order of identifier rank. Should be unique on
	 * IdentifierType.
	 * 
	 * @return
	 */
	SortedSet<Identifier> getIdentifiers();

	/**
	 * The type of entity.
	 * 
	 * @return
	 */
	EntityType getType();

	/**
	 * Associate a fix with this entity. A fix should only be associated with at
	 * most one entity at any one time.
	 * 
	 * @param fix
	 */
	void addFix(Fix fix);

	/**
	 * The latest fix by time for this entity.
	 * 
	 * @return
	 */
	Fix getLatestFix();

	/**
	 * A merge case requires that an entity can move its fixes to another. It
	 * seems wise to have a dedicated method for this action because it may
	 * require significant optimisation given that an entity may have many
	 * fixes.
	 * 
	 * @param entity
	 */
	void moveFixes(Entity entity);
}

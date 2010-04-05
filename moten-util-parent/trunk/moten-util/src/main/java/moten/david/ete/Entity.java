package moten.david.ete;

import java.math.BigDecimal;

public interface Entity {

	/**
	 * Sorted in ascending order of identifier rank. Should be unique on
	 * IdentifierType.
	 * 
	 * @return
	 */
	Identifiers getIdentifiers();

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
	void moveFixesTo(Entity entity);

	/**
	 * The maximum speed of the entity in metres per second.
	 * 
	 * @return
	 */
	BigDecimal getMaximumSpeedMetresPerSecond();

	/**
	 * The minimum period of measurement after which speed calculations are
	 * worth attempting for the given entity.
	 * 
	 * @return
	 */
	BigDecimal getMinimumTimeForSpeedCalculationSeconds();

	/**
	 * Returns true if and only if the fix exists already against the entity.
	 * 
	 * @param fix
	 * @return
	 */
	boolean hasFixAlready(Fix fix);

}

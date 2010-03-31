package moten.david.ete;

import java.math.BigDecimal;
import java.util.Calendar;
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
	 * Return the latest fix before a given time for this entity.
	 * 
	 * @param calendar
	 * @return
	 */
	Fix getLatestFixBefore(Calendar calendar);

	/**
	 * A merge case requires that an entity can move its fixes to another. It
	 * seems wise to have a dedicated method for this action because it may
	 * require significant optimisation given that an entity may have many
	 * fixes.
	 * 
	 * @param entity
	 */
	void moveFixes(Entity entity);

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

	boolean hasFixAlready(Fix fix);

}

package moten.david.ete.track;

import java.util.Calendar;
import java.util.List;

import moten.david.ete.Entity;
import moten.david.ete.Fix;

public interface TrackedEntity extends Entity {
	/**
	 * Return the latest fix at or before a given time for this entity.
	 * 
	 * @param calendar
	 * @return
	 */
	Fix getLatestFixAtOrBefore(Calendar calendar);

	/**
	 * Return the latest fix before a given time for this entity.
	 * 
	 * @param calendar
	 * @return
	 */
	Fix getFirstFixAfter(Calendar calendar);

	/**
	 * Returns the oldest fix against the entity.
	 * 
	 * @return
	 */
	public Fix getOldestFix();

	/**
	 * Get fixes between a and b including a and b.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public List<? extends Fix> getFixesBetween(Fix a, Fix b);

	/**
	 * Get all fixes for the entity in ascending order of time.
	 * 
	 * @return
	 */
	public List<? extends Fix> getFixes();
}

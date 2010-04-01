package moten.david.ete.memory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;
import moten.david.util.collections.CollectionsUtil;

public class MyEntity implements Entity {

	private final TreeSet<MyFix> fixes = new TreeSet<MyFix>();
	private final MyIdentifiers identifiers;
	private transient List<EntityListener> listeners;

	public MyEntity(MyIdentifiers identifiers, List<EntityListener> listeners) {
		this.identifiers = identifiers;
		this.listeners = listeners;
		identifiers.addListener(new MyIdentifiersListener() {

			@Override
			public void added(Identifier identifier) {
				fireIdentifierAdded(identifier);
			}

			@Override
			public void removed(Identifier identifier) {
				fireIdentifierRemoved(identifier);
			}
		});
	}

	private void fireIdentifierAdded(Identifier identifier) {

	}

	private void fireIdentifierRemoved(Identifier identifier) {

	}

	public synchronized void addListener(EntityListener l) {
		if (listeners == null)
			listeners = new ArrayList<EntityListener>();
		listeners.add(l);
	}

	@Override
	public void addFix(Fix fix) {
		synchronized (fixes) {
			fixes.add((MyFix) fix);
			fireFixAdded(fix);
		}
	}

	private void fireFixAdded(Fix fix) {
		if (listeners != null)
			for (EntityListener l : listeners)
				l.fixAdded(this, fix);
	}

	@Override
	public SortedSet<Identifier> getIdentifiers() {
		return identifiers;
	}

	@Override
	public Fix getLatestFix() {
		synchronized (fixes) {
			return fixes.last();
		}
	}

	public Fix getOldestFix() {
		synchronized (fixes) {
			if (fixes.size() == 0)
				return null;
			else
				return fixes.first();
		}
	}

	@Override
	public Fix getLatestFixBefore(Calendar calendar) {
		synchronized (fixes) {
			Fix fix = new MyFix(
					new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO), calendar);
			return fixes.floor((MyFix) fix);
		}
	}

	@Override
	public BigDecimal getMaximumSpeedMetresPerSecond() {
		return BigDecimal.valueOf(20);
	}

	@Override
	public BigDecimal getMinimumTimeForSpeedCalculationSeconds() {
		return BigDecimal.valueOf(60);
	}

	@Override
	public void moveFixes(Entity entity) {
		synchronized (fixes) {
			((MyEntity) entity).fixes.addAll(fixes);
			fixes.clear();
		}
	}

	@Override
	public boolean hasFixAlready(Fix fix) {
		return fixes.contains(fix);
	}

	public void removeOldestFix() {
		synchronized (fixes) {
			fixes.remove(fixes.first());
		}
	}

	/**
	 * Return an enumeration of all the fixes sorted in ascending order of time.
	 * 
	 * @return
	 */
	public Enumeration<MyFix> getFixes() {
		return CollectionsUtil.toEnumeration(fixes.iterator());
	}

}

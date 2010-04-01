package moten.david.ete.memory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;

public class MyEntity implements Entity {

	private static final int MAX_FIXES = 50000;
	private final TreeSet<Fix> fixes = new TreeSet<Fix>();
	private final SortedSet<Identifier> identifiers;
	private transient List<EntityListener> listeners;

	public MyEntity(SortedSet<Identifier> identifiers) {
		this.identifiers = identifiers;
	}

	public synchronized void addListener(EntityListener l) {
		if (listeners == null)
			listeners = new ArrayList<EntityListener>();
		listeners.add(l);
	}

	@Override
	public void addFix(Fix fix) {
		synchronized (fixes) {
			fixes.add(fix);
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
			return fixes.first();
		}
	}

	@Override
	public Fix getLatestFixBefore(Calendar calendar) {
		synchronized (fixes) {
			Fix fix = new MyFix(
					new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO), calendar);
			return fixes.floor(fix);
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
}

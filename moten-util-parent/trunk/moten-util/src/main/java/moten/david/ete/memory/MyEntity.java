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
import moten.david.ete.Identifiers;
import moten.david.ete.memory.event.FixAdded;
import moten.david.ete.track.TrackedEntity;
import moten.david.util.controller.Controller;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MyEntity implements TrackedEntity {

	private final TreeSet<MyFix> fixes = new TreeSet<MyFix>();
	private final MyIdentifiers identifiers;
	private final Controller controller;

	@Inject
	public MyEntity(MyIdentifiersFactory identifiersFactory,
			Controller controller, @Assisted SortedSet<? extends Identifier> ids) {
		this.controller = controller;
		this.identifiers = identifiersFactory.create(ids, this);
	}

	@Override
	public void addFix(Fix fix) {
		synchronized (fixes) {
			fixes.add((MyFix) fix);
			controller.event(new FixAdded(this, (MyFix) fix));
		}
	}

	@Override
	public String toString() {
		return "[identifiers=" + identifiers + "]";
	}

	@Override
	public Identifiers getIdentifiers() {
		return identifiers;
	}

	@Override
	public Fix getLatestFix() {
		synchronized (fixes) {
			return fixes.last();
		}
	}

	@Override
	public Fix getOldestFix() {
		synchronized (fixes) {
			if (fixes.size() == 0)
				return null;
			else
				return fixes.first();
		}
	}

	@Override
	public Fix getLatestFixAtOrBefore(Calendar calendar) {
		synchronized (fixes) {
			Fix fix = new MyFix(null, null, calendar);
			return fixes.floor((MyFix) fix);
		}
	}

	@Override
	public Fix getFirstFixAfter(Calendar calendar) {
		synchronized (fixes) {
			Fix fix = new MyFix(null, null, calendar);
			return fixes.higher((MyFix) fix);
		}
	}

	@Override
	public List<? extends Fix> getFixesBetween(Fix a, Fix b) {
		synchronized (fixes) {
			return new ArrayList<MyFix>(fixes.subSet((MyFix) a, (MyFix) b));
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
	 * Return a list of all the fixes sorted in ascending order of time.
	 * 
	 * @return
	 */
	@Override
	public List<? extends Fix> getFixes() {
		synchronized (fixes) {
			return new ArrayList<MyFix>(fixes);
		}
	}

}

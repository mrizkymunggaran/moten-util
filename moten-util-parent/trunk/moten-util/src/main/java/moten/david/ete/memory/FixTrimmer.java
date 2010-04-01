package moten.david.ete.memory;

import java.util.Enumeration;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;

import com.google.inject.Inject;

public class FixTrimmer implements EntityListener {

	private final long MAX_FIXES = 1000000;
	private final Engine engine;

	@Inject
	public FixTrimmer(Engine engine) {
		this.engine = engine;
	}

	private long fixCount = 0;
	private MyEntity oldestFixEntity = null;

	@Override
	public synchronized void fixAdded(MyEntity entity, Fix fix) {
		fixCount++;
		if (oldestFixEntity == null)
			scanAllEntities();
		else
			processFix(entity, fix);
	}

	private void scanAllEntities() {
		Enumeration<Entity> e = engine.getEntities();
		while (e.hasMoreElements()) {
			MyEntity entity = (MyEntity) e.nextElement();
			processFix(entity, entity.getOldestFix());
		}
	}

	private void processFix(MyEntity entity, Fix fix) {
		if (oldestFixEntity == null)
			oldestFixEntity = entity;
		else if (oldestFixEntity.getOldestFix().getTime().after(fix.getTime()))
			oldestFixEntity = entity;
		if (fixCount > MAX_FIXES)
			oldestFixEntity.removeOldestFix();
	}
}

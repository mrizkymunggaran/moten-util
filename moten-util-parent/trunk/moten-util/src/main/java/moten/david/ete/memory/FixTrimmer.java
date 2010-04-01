package moten.david.ete.memory;

import java.util.Enumeration;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;

import com.google.inject.Inject;

public class FixTrimmer implements EntityListener {

	private final Engine engine;
	private final long maxFixes;

	@Inject
	public FixTrimmer(Engine engine, long maxFixes) {
		this.engine = engine;
		this.maxFixes = maxFixes;
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
		else if (oldestFixEntity.getOldestFix() == null
				|| oldestFixEntity.getOldestFix().getTime()
						.after(fix.getTime()))
			oldestFixEntity = entity;
		if (fixCount > maxFixes)
			oldestFixEntity.removeOldestFix();
	}

	public void entityRemoved(Entity entity) {
		if (oldestFixEntity == entity)
			oldestFixEntity = null;
	}
}

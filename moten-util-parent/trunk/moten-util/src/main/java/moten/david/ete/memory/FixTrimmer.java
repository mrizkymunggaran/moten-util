package moten.david.ete.memory;

import java.util.Enumeration;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.memory.event.FixAdded;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import com.google.inject.Inject;

public class FixTrimmer {

	private final Engine engine;
	private final long maxFixes = 10000;

	@Inject
	public FixTrimmer(Engine engine, Controller controller) {
		this.engine = engine;
		controller.addListener(FixAdded.class,
				new ControllerListener<FixAdded>() {

					@Override
					public void event(FixAdded event) {
						fixAdded(event.getEntity(), event.getFix());
					}
				});
	}

	private long fixCount = 0;
	private MyEntity oldestFixEntity = null;

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

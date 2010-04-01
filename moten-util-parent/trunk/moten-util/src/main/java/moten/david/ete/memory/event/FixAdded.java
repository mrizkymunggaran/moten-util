package moten.david.ete.memory.event;

import moten.david.ete.memory.MyEntity;
import moten.david.ete.memory.MyFix;
import moten.david.util.controller.Event;

public class FixAdded implements Event {

	private final MyEntity entity;
	private final MyFix fix;

	public FixAdded(MyEntity entity, MyFix fix) {
		super();
		this.entity = entity;
		this.fix = fix;
	}

	public MyEntity getEntity() {
		return entity;
	}

	public MyFix getFix() {
		return fix;
	}

}

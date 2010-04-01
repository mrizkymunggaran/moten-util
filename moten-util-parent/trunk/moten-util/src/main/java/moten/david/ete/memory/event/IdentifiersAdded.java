package moten.david.ete.memory.event;

import moten.david.ete.memory.MyEntity;
import moten.david.ete.memory.MyIdentifier;
import moten.david.util.controller.Event;

public class IdentifiersAdded implements Event {
	private final MyEntity entity;
	private final MyIdentifier identifier;

	public MyEntity getEntity() {
		return entity;
	}

	public MyIdentifier getIdentifier() {
		return identifier;
	}

	public IdentifiersAdded(MyEntity entity, MyIdentifier identifier) {
		super();
		this.entity = entity;
		this.identifier = identifier;
	}
}

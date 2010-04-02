package moten.david.ete.memory;

import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Identifier;
import moten.david.ete.memory.event.IdentifierAdded;
import moten.david.ete.memory.event.IdentifierRemoved;
import moten.david.util.controller.Controller;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MyIdentifiers extends TreeSet<Identifier> {

	private static final long serialVersionUID = -1849220137647917437L;
	private final Controller controller;
	private final MyEntity entity;

	@Inject
	public MyIdentifiers(Controller controller,
			@Assisted SortedSet<Identifier> identifiers,
			@Assisted MyEntity entity) {
		this.controller = controller;
		this.entity = entity;
		for (Identifier identifier : identifiers)
			add(identifier);
	}

	@Override
	public boolean add(Identifier e) {
		boolean result = super.add(e);
		if (result)
			fireAdded(e);
		return result;
	}

	private void fireAdded(Identifier id) {
		controller.event(new IdentifierAdded(entity, (MyIdentifier) id));
	}

	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		if (result)
			fireRemoved((Identifier) o);
		return result;
	}

	private void fireRemoved(Identifier id) {
		controller.event(new IdentifierRemoved(entity, (MyIdentifier) id));
	}
}

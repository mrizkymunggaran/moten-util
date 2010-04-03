package moten.david.ete.memory;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Identifier;
import moten.david.ete.Identifiers;
import moten.david.ete.memory.event.IdentifierAdded;
import moten.david.ete.memory.event.IdentifierRemoved;
import moten.david.util.controller.Controller;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MyIdentifiers implements Identifiers {

	private static final long serialVersionUID = -1849220137647917437L;
	private final Controller controller;
	private final MyEntity entity;
	private final TreeSet<Identifier> identifiers = new TreeSet<Identifier>();

	@Inject
	public MyIdentifiers(Controller controller,
			@Assisted SortedSet<? extends Identifier> identifiers,
			@Assisted MyEntity entity) {
		this.controller = controller;
		this.entity = entity;
		for (Identifier identifier : identifiers)
			add(identifier);
	}

	public void add(Identifier e) {
		boolean result = identifiers.add(e);
		if (result)
			fireAdded(e);
		else
			throw new RuntimeException("could not add identifier!");
	}

	private void fireAdded(Identifier id) {
		controller.event(new IdentifierAdded(entity, (MyIdentifier) id));
	}

	@Override
	public void remove(Identifier id) {
		boolean result = identifiers.remove(id);
		if (result)
			fireRemoved(id);
		else
			throw new RuntimeException("coudl not remove identifier!");
	}

	private void fireRemoved(Identifier id) {
		controller.event(new IdentifierRemoved(entity, (MyIdentifier) id));
	}

	@Override
	public SortedSet<Identifier> set() {
		return Collections.unmodifiableSortedSet(identifiers);
	}
}

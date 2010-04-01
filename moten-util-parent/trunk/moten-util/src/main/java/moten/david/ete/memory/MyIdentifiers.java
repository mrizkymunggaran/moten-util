package moten.david.ete.memory;

import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Identifier;

public class MyIdentifiers extends TreeSet<Identifier> {

	private static final long serialVersionUID = -1849220137647917437L;

	public MyIdentifiers(SortedSet<Identifier> identifiers) {
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

	private void fireAdded(Identifier e) {
		// TODO
	}

	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		if (result)
			fireRemoved((Identifier) o);
		return result;
	}

	private void fireRemoved(Identifier identifier) {
		// TODO
	}

}

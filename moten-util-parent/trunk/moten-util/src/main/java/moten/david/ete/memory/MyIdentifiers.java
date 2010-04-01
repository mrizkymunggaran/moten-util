package moten.david.ete.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Identifier;

public class MyIdentifiers extends TreeSet<Identifier> {

	private static final long serialVersionUID = -1849220137647917437L;

	private List<MyIdentifiersListener> listeners;

	public MyIdentifiers(SortedSet<Identifier> identifiers) {
		for (Identifier identifier : identifiers)
			add(identifier);
	}

	public void addListener(MyIdentifiersListener l) {
		if (listeners == null)
			listeners = new ArrayList<MyIdentifiersListener>();
		listeners.add(l);
	}

	@Override
	public boolean add(Identifier e) {
		boolean result = super.add(e);
		if (result)
			fireAdded(e);
		return result;
	}

	private void fireAdded(Identifier e) {
		if (listeners != null)
			for (MyIdentifiersListener l : listeners)
				l.added(e);
	}

	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		if (result)
			fireRemoved((Identifier) o);
		return result;
	}

	private void fireRemoved(Identifier identifier) {
		if (listeners != null)
			for (MyIdentifiersListener l : listeners)
				l.removed(identifier);
	}

}

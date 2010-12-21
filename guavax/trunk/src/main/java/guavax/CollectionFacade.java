package guavax;

import java.util.Collection;
import java.util.Iterator;

class CollectionFacade<T> implements Collection<T> {
    private final Collection<T> c;

    public CollectionFacade(Collection<T> c) {
	this.c = c;
    }

    @Override
    public boolean add(T arg0) {
	return c.add(arg0);
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
	return c.addAll(arg0);
    }

    @Override
    public void clear() {
	c.clear();
    }

    @Override
    public boolean contains(Object arg0) {
	return c.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
	return c.containsAll(arg0);
    }

    @Override
    public boolean isEmpty() {
	return c.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
	return c.iterator();
    }

    @Override
    public boolean remove(Object arg0) {
	return c.remove(arg0);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
	return c.remove(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
	return c.retainAll(arg0);
    }

    @Override
    public int size() {
	return c.size();
    }

    @Override
    public Object[] toArray() {
	return c.toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(T[] arg0) {
	return c.toArray(arg0);
    }

}

package moten.david.util.concurrent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Provides threadLocal storage into a map
 * 
 * @author dxm
 * 
 * @param <S>
 * @param <T>
 */
public class ThreadLocalHashMap<S, T> implements Map<S, T> {

    private ThreadLocal<HashMap<S, T>> threadLocal = new ThreadLocal<HashMap<S, T>>();

    @Override
    public void clear() {
        threadLocal = new ThreadLocal<HashMap<S, T>>();
    }

    private synchronized Map<S, T> getMap() {
        if (threadLocal.get() == null)
            threadLocal.set(new HashMap<S, T>());
        return threadLocal.get();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return getMap().containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return getMap().containsValue(arg0);
    }

    @Override
    public Set<java.util.Map.Entry<S, T>> entrySet() {
        return getMap().entrySet();
    }

    @Override
    public T get(Object arg0) {
        return getMap().get(arg0);
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public Set<S> keySet() {
        return getMap().keySet();
    }

    @Override
    public T put(S arg0, T arg1) {
        return getMap().put(arg0, arg1);
    }

    @Override
    public void putAll(Map<? extends S, ? extends T> arg0) {
        getMap().putAll(arg0);
    }

    @Override
    public T remove(Object arg0) {
        return getMap().remove(arg0);
    }

    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public Collection<T> values() {
        return getMap().values();
    }

}

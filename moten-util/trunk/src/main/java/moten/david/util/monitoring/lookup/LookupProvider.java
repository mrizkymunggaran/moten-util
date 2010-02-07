package moten.david.util.monitoring.lookup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.inject.Provider;

public class LookupProvider<T> implements Provider<T> {

	private final Class<T> cls;

	public LookupProvider(Class<T> cls, String key) {
		this.cls = cls;
		this.key = key;
	}

	private static final ThreadLocal<Lookup> threadLocal = new ThreadLocal<Lookup>();

	public static void setLookup(Lookup map) {
		threadLocal.set(map);
	}

	private final String key;

	@Override
	public T get() {
		Lookup lookup = threadLocal.get();
		if (lookup == null)
			throw new RuntimeException("map has not been set");
		try {
			return (T) getSingleStringConstructor(cls.getConstructors())
					.newInstance(lookup.get(key));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Constructor getSingleStringConstructor(Constructor<?>[] constructors) {
		for (Constructor constructor : constructors) {
			if (constructor.getParameterTypes().length == 1
					&& constructor.getParameterTypes()[0].equals(String.class))
				return constructor;
		}
		throw new RuntimeException(
				"didn't find single argument String constructor");

	}

	@Override
	public String toString() {
		return "${" + key + "}";
	}
}

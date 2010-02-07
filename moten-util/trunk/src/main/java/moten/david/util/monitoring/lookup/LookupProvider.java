package moten.david.util.monitoring.lookup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.inject.Provider;

/**
 * Provides threadLocal storage of a lookup based on a String key. T must
 * satisfy that it has a constructor that takes a single string parameter.
 * 
 * @author dave
 * 
 * @param <T>
 */
public class LookupProvider<T> implements Provider<T> {
	private final Class<T> cls;

	public LookupProvider(Class<T> cls, String key) {
		this.cls = cls;
		this.key = key;
	}

	private static final ThreadLocal<Lookup> threadLocal = new ThreadLocal<Lookup>();

	/**
	 * Being static this makes the assumption that every LookupProvider being
	 * used will use the same source
	 * 
	 * @param lookup
	 */
	public static void setLookup(Lookup lookup) {
		threadLocal.set(lookup);
	}

	private final String key;

	@Override
	public T get() {
		Lookup lookup = threadLocal.get();
		if (lookup == null)
			throw new RuntimeException("map has not been set");
		try {
			String value = lookup.get(key);
			if (value == null)
				return null;
			else
				return (T) getSingleStringConstructor(cls.getConstructors())
						.newInstance(value);
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

	private Constructor<?> getSingleStringConstructor(
			Constructor<?>[] constructors) {
		for (Constructor<?> constructor : constructors) {
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

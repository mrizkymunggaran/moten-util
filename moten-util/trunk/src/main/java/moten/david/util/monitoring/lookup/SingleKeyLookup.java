package moten.david.util.monitoring.lookup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.inject.Provider;

/**
 * Returns keyed value of type T. T must satisfy that it has a constructor that
 * takes a single string parameter.
 * 
 * get method returns the string value corresponding to the constructor
 * parameter key converted to type T
 * 
 * @author dave
 * 
 * @param <T>
 */
public class SingleKeyLookup<T> implements Provider<T> {
	private final Class<T> cls;
	private final Lookup lookup;

	public SingleKeyLookup(Class<T> cls, String key, Lookup lookup) {
		this.cls = cls;
		this.key = key;
		this.lookup = lookup;
	}

	public String getKey() {
		return key;
	}

	private final String key;

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
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

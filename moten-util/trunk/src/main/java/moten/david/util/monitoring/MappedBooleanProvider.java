package moten.david.util.monitoring;

import java.util.Map;

import com.google.inject.Provider;

public class MappedBooleanProvider implements Provider<Boolean> {

	private static final ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();

	public static void setMap(Map<String, String> map) {
		threadLocal.set(map);
	}

	private final String key;

	public MappedBooleanProvider(String key) {
		this.key = key;
	}

	@Override
	public Boolean get() {
		Map<String, String> map = threadLocal.get();
		if (map == null)
			throw new RuntimeException("map has not been set");
		return new Boolean(map.get(key));
	}

	@Override
	public String toString() {
		return "${" + key + "}";
	}

}

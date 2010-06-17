package moten.david.util.monitoring.lookup;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MapLookup implements Lookup {

	private final Map<String, String> map;

	@Inject
	public MapLookup(@Assisted Map<String, String> map) {
		this.map = map;
	}

	public void put(String key, String value) {
		map.put(key, value);
	}

	@Override
	public String get(String context, String key) {
		return map.get(key);
	}

}

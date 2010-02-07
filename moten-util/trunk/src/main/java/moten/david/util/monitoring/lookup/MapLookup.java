package moten.david.util.monitoring.lookup;

import java.util.Map;

public class MapLookup implements Lookup {

	private final Map<String, String> map;

	public MapLookup(Map<String, String> map) {
		this.map = map;
	}

	@Override
	public String get(String key) {
		return map.get(key);
	}

}

package moten.david.util.monitoring.test;

import java.util.HashMap;
import java.util.Map;

import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.MapLookup;
import moten.david.util.monitoring.lookup.MapLookupFactory;

import com.google.inject.Inject;

public class ConfigurationLookup implements Lookup {

	private final MapLookup lookup;

	@Inject
	public ConfigurationLookup(MapLookupFactory mapLookupFactory) {
		Map<String, String> conf = createMap("minimumValue", "23", "enabled",
				"false");
		lookup = mapLookupFactory.create(conf);
	}

	@Override
	public String get(String context, String key) {
		return lookup.get(context, key);
	}

	private Map<String, String> createMap(String... items) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < items.length; i += 2) {
			map.put(items[i], items[i + 1]);
		}
		return map;
	}

}

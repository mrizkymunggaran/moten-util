package moten.david.util.monitoring.test;

import java.util.Map;

import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.MapLookup;
import moten.david.util.monitoring.lookup.MapLookupFactory;

import com.google.inject.Inject;

public class ConfigurationLookup implements Lookup {

	private final MapLookup lookup;

	@Inject
	public ConfigurationLookup(MapLookupFactory mapLookupFactory) {
		Map<String, String> conf = MapLookupFactory.Util.createMap(
				"minimumValue", "23", "enabled", "false");
		lookup = mapLookupFactory.create(conf);
	}

	@Override
	public String get(String context, String key) {
		return lookup.get(context, key);
	}

}

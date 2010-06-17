package moten.david.util.monitoring.lookup;

import java.util.HashMap;
import java.util.Map;

/**
 * Facilitates guice instantiation of map lookups which means aop enabled
 * 
 * @author dxm
 * 
 */
public interface MapLookupFactory {
	MapLookup create(Map<String, String> map);

	public static class Util {
		public static Map<String, String> createMap(String... items) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < items.length; i += 2) {
				map.put(items[i], items[i + 1]);
			}
			return map;
		}
	}
}

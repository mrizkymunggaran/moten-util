package moten.david.util.monitoring.lookup;

import java.util.Map;

/**
 * Facilitates guice instantiation of map lookups which means aop enabled
 * 
 * @author dxm
 * 
 */
public interface MapLookupFactory {
	MapLookup create(Map<String, String> map);
}

package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.Map;

import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;

public class MonitoringLookups {

	private final LookupType defaultType;

	public MonitoringLookups(LookupType defaultType) {
		this.defaultType = defaultType;
	}

	public LookupType getDefaultType() {
		return defaultType;
	}

	private final Map<LookupType, ThreadLocal<Lookup>> map = new HashMap<LookupType, ThreadLocal<Lookup>>();

	private synchronized ThreadLocal<Lookup> getThreadLocal(LookupType type) {
		if (map.get(type) == null)
			map.put(type, new ThreadLocal<Lookup>());
		return map.get(type);
	}

	public void setLookup(LookupType type, Lookup lookup) {
		getThreadLocal(type).set(lookup);
	}

	public ThreadLocal<Lookup> getLookupThreadLocal(LookupType type) {
		return getThreadLocal(type);
	}

}

package moten.david.util.monitoring;

import moten.david.util.monitoring.lookup.Lookup;

public class MonitoringLookups {

	private final ThreadLocal<Lookup> monitoringLookupThreadLocal = new ThreadLocal<Lookup>();

	public ThreadLocal<Lookup> getMonitoringLookupThreadLocal() {
		return monitoringLookupThreadLocal;
	}

	public ThreadLocal<Lookup> getConfigurationLookupThreadLocal() {
		return configurationLookupThreadLocal;
	}

	private final ThreadLocal<Lookup> configurationLookupThreadLocal = new ThreadLocal<Lookup>();

	public void setMonitoringLookup(Lookup lookup) {
		monitoringLookupThreadLocal.set(lookup);
	}

	public void setConfigurationLookup(Lookup lookup) {
		configurationLookupThreadLocal.set(lookup);
	}

	public Lookup getConfigurationLookup() {
		return configurationLookupThreadLocal.get();
	}

	public Lookup getMonitoringLookup() {
		return monitoringLookupThreadLocal.get();
	}

}

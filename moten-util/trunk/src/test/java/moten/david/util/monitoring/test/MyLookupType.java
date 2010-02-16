package moten.david.util.monitoring.test;

import moten.david.util.monitoring.lookup.LookupType;

public enum MyLookupType implements LookupType {
	/**
	 * Lookup for the application being monitored
	 */
	APPLICATION,
	/**
	 * Lookup for a combination of application and system wide configuration
	 */
	CONFIGURATION;
}

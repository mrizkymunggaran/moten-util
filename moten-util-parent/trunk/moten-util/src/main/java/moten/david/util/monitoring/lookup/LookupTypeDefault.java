package moten.david.util.monitoring.lookup;


public enum LookupTypeDefault implements LookupType {
	/**
	 * Lookup for the application being monitored
	 */
	APPLICATION,
	/**
	 * Lookup for a combination of application and system wide configuration
	 */
	CONFIGURATION;
}

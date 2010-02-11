package moten.david.util.monitoring.lookup;

import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PropertiesLookup implements Lookup {

	private final Properties properties;

	@Inject
	public PropertiesLookup(@Assisted Properties properties) {
		this.properties = properties;
	}

	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}

}

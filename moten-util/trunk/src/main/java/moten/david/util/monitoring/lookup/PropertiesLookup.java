package moten.david.util.monitoring.lookup;

import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

public class PropertiesLookup implements Lookup {

	private final Provider<Properties> provider;

	@Inject
	public PropertiesLookup(@Assisted Provider<Properties> provider) {
		this.provider = provider;
	}

	@Override
	public String get(String key) {
		return provider.get().getProperty(key);
	}

}

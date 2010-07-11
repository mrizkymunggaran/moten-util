package moten.david.util.monitoring.lookup;

import java.util.Properties;

import com.google.inject.Provider;

public class UrlPropertiesLookup extends PropertiesLookup {

	public UrlPropertiesLookup(Provider<Properties> provider) {
		super(provider);
	}

}

package moten.david.util.monitoring.lookup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.google.inject.Inject;

public class UrlLookup implements Lookup {

	private final CachingUrlPropertiesProvider provider;

	@Inject
	public UrlLookup(CachingUrlPropertiesProvider provider) {
		this.provider = provider;

	}

	@Override
	public String get(String context, String key) {
		try {
			System.out.println("getting " + context + "," + key);
			URL url = new URL(context);
			Properties props = provider.getProperties(url);
			return props.getProperty(key);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

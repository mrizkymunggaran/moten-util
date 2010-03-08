package moten.david.util.monitoring.lookup;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Provider;

public class CachingUrlPropertiesProvider {

	private final Map<URL, Properties> map = new ConcurrentHashMap<URL, Properties>();

	public void reset() {
		map.clear();
	}

	public Provider<Properties> getPropertiesProvider(
			final UrlFactory urlFactory, final String path) {
		return new Provider<Properties>() {

			@Override
			public Properties get() {
				URL url = urlFactory.create(path);
				return getProperties(url);
			}
		};
	}

	protected synchronized Properties getProperties(URL url) {
		if (map.get(url) == null) {
			Properties p = new Properties();
			try {
				p.load(url.openStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			map.put(url, p);
		}
		return map.get(url);
	}
}

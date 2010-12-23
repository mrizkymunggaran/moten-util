package moten.david.util.monitoring.lookup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Provider;

public class CachingUrlPropertiesProvider {

    private final Map<URI, Properties> map = new ConcurrentHashMap<URI, Properties>();

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
        try {
            if (map.get(url.toURI()) == null) {
                Properties p = new Properties();
                try {
                    p.load(url.openStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                map.put(url.toURI(), p);
            }
            return map.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

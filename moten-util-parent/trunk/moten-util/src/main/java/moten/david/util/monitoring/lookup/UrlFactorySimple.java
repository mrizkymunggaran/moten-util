package moten.david.util.monitoring.lookup;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlFactorySimple implements UrlFactory{

	@Override
	public URL create(String path) {
		try {
			return new URL(path);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

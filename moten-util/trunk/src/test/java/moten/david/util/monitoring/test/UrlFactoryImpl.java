package moten.david.util.monitoring.test;

import java.net.URL;

import moten.david.util.monitoring.lookup.UrlFactory;

public class UrlFactoryImpl implements UrlFactory {

	@Override
	public URL create(String path) {
		return getClass().getResource(path);
	}

}

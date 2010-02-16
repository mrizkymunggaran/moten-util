package moten.david.util.monitoring.test;

import java.net.URL;

import moten.david.util.monitoring.lookup.UrlFactory;

/**
 * Given a path returns a Url from the classpath
 * 
 * @author dxm
 * 
 */
public class UrlFactoryClasspath implements UrlFactory {

	@Override
	public URL create(String path) {
		return getClass().getResource(path);
	}

}

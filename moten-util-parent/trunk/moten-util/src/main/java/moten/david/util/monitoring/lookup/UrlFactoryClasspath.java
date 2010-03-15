package moten.david.util.monitoring.lookup;

import java.net.URL;


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

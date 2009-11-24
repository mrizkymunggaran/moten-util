package moten.david.util.tv.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ApplicationInjector {

	/**
	 * singleton design pattern
	 */
	private static Injector injector;

	public synchronized static Injector getInjector() {
		if (injector == null)
			injector = Guice.createInjector(new ApplicationInjectorModule());
		return injector;
	}

}

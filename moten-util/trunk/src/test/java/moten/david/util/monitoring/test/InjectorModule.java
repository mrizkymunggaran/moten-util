package moten.david.util.monitoring.test;

import moten.david.util.monitoring.Util;
import moten.david.util.monitoring.lookup.UrlFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Util.bindDefaults(this.binder());
		bind(UrlFactory.class).to(UrlFactoryImpl.class).in(Scopes.SINGLETON);
	}

}

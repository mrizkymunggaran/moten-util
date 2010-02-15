package moten.david.util.monitoring.test;

import moten.david.util.monitoring.Util;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.UrlFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Util.bindDefaults(this.binder());
		bind(UrlFactory.class).to(UrlFactoryImpl.class).in(Scopes.SINGLETON);
		bind(LookupType.class).annotatedWith(Names.named("default"))
				.toInstance(LookupType.MONITORING);
	}

}

package moten.david.util.monitoring.test;

import moten.david.util.monitoring.Util;

import com.google.inject.AbstractModule;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Util.bindDefaults(this.binder());
	}

}

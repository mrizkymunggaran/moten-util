package moten.david.util.monitoring.gwt.server.dummy;

import java.util.List;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.gwt.server.Bindings;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.UrlLookup;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Bindings.bindDefaults(this.binder());
		bind(new TypeLiteral<List<Check>>() {
		}).to(Checks.class);
		bind(Lookup.class).annotatedWith(Names.named("application")).to(
				UrlLookup.class);
		bind(Lookup.class).annotatedWith(Names.named("configuration")).to(
				ConfigurationLookup.class);

	}

}

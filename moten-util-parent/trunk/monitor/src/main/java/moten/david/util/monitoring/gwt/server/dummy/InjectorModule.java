package moten.david.util.monitoring.gwt.server.dummy;

import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.expression.ExpressionPresenterSingleLine;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Util;
import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.server.Convertor;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.LookupTypeDefault;
import moten.david.util.monitoring.lookup.UrlFactory;
import moten.david.util.monitoring.lookup.UrlFactorySimple;
import moten.david.util.monitoring.lookup.UrlLookup;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Util.bindDefaults(this.binder());
		bind(UrlFactory.class).to(UrlFactorySimple.class).in(
				Scopes.SINGLETON);
		bind(LookupType.class).annotatedWith(Names.named("default"))
				.toInstance(LookupTypeDefault.APPLICATION);
		bind(Lookup.class).annotatedWith(Names.named("application")).to(UrlLookup.class);
		bind(Lookup.class).annotatedWith(Names.named("configuration")).to(ConfigurationLookup.class);
		bind(CachingUrlPropertiesProvider.class);// should not be singleton!
		bind(ExpressionPresenter.class).to(ExpressionPresenterSingleLine.class)
				.in(Scopes.SINGLETON);
		bind(EvaluationContext.class);// should not be singleton!
		bind(Convertor.class).in(Scopes.SINGLETON);
		bind(Checker.class).to(MyChecker.class);//should not be singleton!
		bind(ApplicationService.class).to(ApplicationServiceDummy.class).in(Scopes.SINGLETON);
	}

}

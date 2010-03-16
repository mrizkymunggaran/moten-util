package moten.david.util.monitoring.gwt.server.dummy;

import java.util.List;

import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.expression.ExpressionPresenterSingleLine;
import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.DefaultChecker;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Util;
import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.server.ApplicationServiceGuice;
import moten.david.util.monitoring.gwt.server.Convertor;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.DefaultLookupType;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.UrlFactory;
import moten.david.util.monitoring.lookup.UrlFactorySimple;
import moten.david.util.monitoring.lookup.UrlLookup;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class InjectorModule extends AbstractModule {

    @Override
    protected void configure() {
        Util.bindDefaults(this.binder());
        bind(UrlFactory.class).to(UrlFactorySimple.class).in(Scopes.SINGLETON);
        bind(LookupType.class).annotatedWith(Names.named("default"))
                .toInstance(DefaultLookupType.APPLICATION);
        bind(Lookup.class).annotatedWith(Names.named("application")).to(
                UrlLookup.class);
        bind(Lookup.class).annotatedWith(Names.named("configuration")).to(
                ConfigurationLookup.class);
        bind(CachingUrlPropertiesProvider.class);// should not be singleton!
        bind(ExpressionPresenter.class).to(ExpressionPresenterSingleLine.class)
                .in(Scopes.SINGLETON);
        bind(EvaluationContext.class);// should not be singleton!
        bind(Convertor.class).in(Scopes.SINGLETON);
        bind(Checker.class).to(DefaultChecker.class);// should not be singleton!
        bind(new TypeLiteral<List<Check>>() {
        }).to(Checks.class);
        bind(ApplicationService.class).to(ApplicationServiceGuice.class).in(
                Scopes.SINGLETON);
    }

}

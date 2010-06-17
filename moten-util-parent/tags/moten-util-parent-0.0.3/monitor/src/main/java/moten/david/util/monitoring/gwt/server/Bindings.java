package moten.david.util.monitoring.gwt.server;

import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.expression.ExpressionPresenterSingleLine;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.DefaultChecker;
import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.Util;
import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.lookup.CachingUrlPropertiesProvider;
import moten.david.util.monitoring.lookup.DefaultLookupType;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.UrlFactory;
import moten.david.util.monitoring.lookup.UrlFactorySimple;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class Bindings{

    public static void bindDefaults(Binder binder) {
        Util.bindDefaults(binder);
        binder.bind(UrlFactory.class).to(UrlFactorySimple.class).in(Scopes.SINGLETON);
        binder.bind(LookupType.class).annotatedWith(Names.named("default"))
                .toInstance(DefaultLookupType.APPLICATION);
        binder.bind(CachingUrlPropertiesProvider.class);// should not be singleton!
        binder.bind(EvaluationContext.class);// should not be singleton!
        binder.bind(Checker.class).to(DefaultChecker.class);// should not be singleton!
        binder.bind(ExpressionPresenter.class).to(ExpressionPresenterSingleLine.class)
        .in(Scopes.SINGLETON);
        binder.bind(Convertor.class).in(Scopes.SINGLETON);        
        binder.bind(ApplicationService.class).to(ApplicationServiceGuice.class).in(
                Scopes.SINGLETON);
    }

}

package moten.david.util.monitoring.gwt.server.dummy;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class Tester {

    @Inject
    private MyChecker checker;

    public Tester() {
        Injector injector = Guice.createInjector(new InjectorModule());
        injector.injectMembers(this);
    }

    public static void main(String[] args) {
        new Tester();
    }
}

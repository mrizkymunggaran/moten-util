package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.server.dummy.InjectorModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class ApplicationServiceGuiceProvider implements
        ApplicationServiceProvider {

    private final Injector injector;

    public ApplicationServiceGuiceProvider() {
        String injectorModuleName = System
                .getProperty("moten.david.monitor.guice.module");
        Module injectorModule;
        if (injectorModuleName == null)
            injectorModule = new InjectorModule();
        else
            try {
                injectorModule = (Module) Class.forName(injectorModuleName)
                        .newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        injector = Guice.createInjector(injectorModule);
    }

    @Override
    public ApplicationService get() {
        return injector.getInstance(ApplicationService.class);
    }

}

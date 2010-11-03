package moten.david.ets.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Guice initializer for servlets.
 * 
 * @author dave
 * 
 */
public class MyGuiceServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice
                .createInjector(new MyServletModule(), new InjectorModule());
    }
}
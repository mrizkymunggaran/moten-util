package moten.david.util.monitoring.gwt.server;

import java.util.logging.Logger;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
        ApplicationService {

    private static Logger log = Logger.getLogger(ApplicationServiceImpl.class
            .getName());

    private static final long serialVersionUID = -7459556404523295829L;

    private ApplicationService service;

    public ApplicationServiceImpl() {
        String className = System.getProperty("serviceProvider");
        if (className == null) {
            className = ApplicationServiceDummyProvider.class.getName();
            log
                    .info("System property serviceProvider not set, using dummy provider instead");
        }
        log.info("instantiating ApplicationServiceProvider " + className);
        try {
            ApplicationServiceProvider provider = (ApplicationServiceProvider) Class
                    .forName(className).newInstance();
            service = provider.get();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void check() {
        service.check();

    }

    @Override
    public String getApplicationName() {
        return service.getApplicationName();
    }

    @Override
    public AppChecks getResults() {
        return service.getResults();
    }

}

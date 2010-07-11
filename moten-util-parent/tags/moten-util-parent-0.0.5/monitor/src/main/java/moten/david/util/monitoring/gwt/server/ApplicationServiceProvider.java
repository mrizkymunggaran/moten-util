package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.gwt.client.ApplicationService;

public interface ApplicationServiceProvider {
    ApplicationService get();
}

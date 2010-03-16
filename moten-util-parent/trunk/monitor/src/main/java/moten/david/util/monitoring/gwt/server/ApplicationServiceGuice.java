package moten.david.util.monitoring.gwt.server;

import moten.david.util.monitoring.Checker;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ApplicationServiceGuice extends ApplicationServiceBase {

    @Inject
    public ApplicationServiceGuice(Provider<Checker> checkerProvider,
            Convertor convertor) {
        super(checkerProvider, convertor);
    }

}

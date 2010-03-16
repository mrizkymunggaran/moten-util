package moten.david.util.monitoring.gwt.server;

import java.util.Map;
import java.util.logging.Logger;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.inject.Provider;

public class ApplicationServiceBase implements ApplicationService {

    private static Logger log = Logger.getLogger(ApplicationServiceBase.class
            .getName());
    private final Provider<Checker> checkerProvider;
    private final Convertor convertor;

    public ApplicationServiceBase(Provider<Checker> checkerProvider,
            Convertor convertor) {
        this.checkerProvider = checkerProvider;
        this.convertor = convertor;
    }

    @Override
    public String getApplicationName() {
        return "application";
    }

    @Override
    public void check() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * moten.david.util.monitoring.gwt.client.ApplicationService#getResults()
     * 
     * Don't need to do exception handling because ApplicationServiceImpl calls
     * this and handles exceptions.
     */
    @Override
    public AppChecks getResults() {
        Checker checker = checkerProvider.get();
        Map<Check, CheckResult> results = checker.check();
        return convertor.createAppChecks(checker.getChecks(), results);
    }

}

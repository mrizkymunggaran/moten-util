package moten.david.util.monitoring.gwt.server.dummy;

import java.util.Map;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.CheckResult;
import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppChecks;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ApplicationServiceDummy implements ApplicationService {
	private final Provider<Checker> checkerProvider;
	private final Convertor convertor;

	@Inject
	public ApplicationServiceDummy(Provider<Checker> checkerProvider,Convertor convertor) {
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

	@Override
	public AppChecks getResults() {
		Checker checker = checkerProvider.get();
		Map<Check, CheckResult> results = checker.check();
		return convertor.getAppChecks(results);
	}

}

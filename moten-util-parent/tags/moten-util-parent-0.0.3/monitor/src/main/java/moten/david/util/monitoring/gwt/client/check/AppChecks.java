package moten.david.util.monitoring.gwt.client.check;

import java.io.Serializable;

public class AppChecks implements Serializable {

	private static final long serialVersionUID = 395450876893590104L;
	private AppCheck[] checks;
	private AppCheckResult[] results;

	public AppCheck[] getChecks() {
		return checks;
	}

	public void setChecks(AppCheck[] checks) {
		this.checks = checks;
	}

	public AppCheckResult[] getResults() {
		return results;
	}

	public void setResults(AppCheckResult[] results) {
		this.results = results;
	}

}

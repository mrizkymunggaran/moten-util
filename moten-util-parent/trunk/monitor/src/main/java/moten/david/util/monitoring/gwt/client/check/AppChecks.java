package moten.david.util.monitoring.gwt.client.check;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AppChecks implements Serializable {

    private static final long serialVersionUID = 395450876893590104L;
    private List<AppCheck> checks;
    private Map<String, AppCheckResult> results;

    public List<AppCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<AppCheck> checks) {
        this.checks = checks;
    }

    public Map<String, AppCheckResult> getResults() {
        return results;
    }

    public void setResults(Map<String, AppCheckResult> results) {
        this.results = results;
    }

}

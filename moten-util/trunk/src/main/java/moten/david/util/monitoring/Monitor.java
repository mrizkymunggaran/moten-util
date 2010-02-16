package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moten.david.util.monitoring.lookup.LookupType;

public class Monitor {

	private final List<Check> checks;
	private final Level unknown;
	private final Level ok;
	private final EvaluationContext expressions;
	private final Level exception;

	public Monitor(EvaluationContext expressions, List<Check> checks, Level ok,
			Level unknown, Level exception) {
		this.expressions = expressions;
		this.checks = checks;
		this.ok = ok;
		this.unknown = unknown;
		this.exception = exception;
	}

	public Map<Check, CheckResult> check() {
		Map<Check, CheckResult> map = new HashMap<Check, CheckResult>();
		for (Check check : checks) {
			check(map, check);
		}
		return map;
	}

	private CheckResult check(Map<Check, CheckResult> map, Check check) {

		if (map.get(check) == null) {
			boolean depsOk = true;
			if (check.getDependencies() != null)
				for (Check dep : check.getDependencies()) {
					CheckResult result = check(map, dep);
					if (!result.getLevel().equals(ok))
						depsOk = false;
				}
			if (!depsOk)
				map.put(check, new CheckResult(unknown));
			else {
				// set up the lookups
				MonitoringLookups lookups = expressions.getLookups();
				for (LookupType type : check.getLookups().keySet())
					lookups.put(type, check.getLookups().get(type));
				try {
					if (check.getExpression().evaluate())
						map.put(check, new CheckResult(ok));
					else
						map
								.put(check, new CheckResult(check
										.getFailureLevel()));
				} catch (RuntimeException e) {
					map.put(check, new CheckResult(exception, e));
				}
			}
		}
		return map.get(check);
	}
}

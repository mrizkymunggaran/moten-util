package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moten.david.util.expression.Expressions;
import moten.david.util.monitoring.lookup.LookupType;

public class Monitor {

	private final List<Check> checks;
	private final Level unknown;
	private final Level ok;
	private final Expressions expressions;

	public Monitor(Expressions expressions, List<Check> checks, Level ok,
			Level unknown) {
		this.expressions = expressions;
		this.checks = checks;
		this.ok = ok;
		this.unknown = unknown;
	}

	public Map<Check, Level> check() {
		Map<Check, Level> map = new HashMap<Check, Level>();
		for (Check check : checks) {
			check(map, check);
		}
		return map;
	}

	private Level check(Map<Check, Level> map, Check check) {

		if (map.get(check) == null) {
			boolean depsOk = true;
			if (check.getDependencies() != null)
				for (Check dep : check.getDependencies()) {
					Level level = check(map, dep);
					if (!level.equals(ok))
						depsOk = false;
				}
			if (!depsOk)
				map.put(check, unknown);
			else {
				// set up the lookups
				MonitoringLookups lookups = expressions.getLookups();
				for (LookupType type : check.getLookups().keySet())
					lookups.put(type, check.getLookups().get(type));

				if (check.getExpression().evaluate())
					map.put(check, ok);
				else
					map.put(check, check.getFailureLevel());
			}
		}
		return map.get(check);
	}
}

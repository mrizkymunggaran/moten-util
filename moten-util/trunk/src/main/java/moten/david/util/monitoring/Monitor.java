package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moten.david.util.expression.Util;
import moten.david.util.monitoring.lookup.LookupType;

public class Monitor {

	private final List<Check> checks;
	private final Level unknown;
	private final Level ok;

	public Monitor(List<Check> checks, Level ok, Level unknown) {
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
				MonitoringLookups lookups = new MonitoringLookups(check
						.getLookupTypeDefault());
				for (LookupType type : check.getLookups().keySet())
					lookups.setLookup(type, check.getLookups().get(type));
				Util.setLookups(lookups);

				if (check.getExpression().evaluate())
					map.put(check, ok);
				else
					map.put(check, check.getFailureLevel());
			}
		}
		return map.get(check);
	}
}

package moten.david.util.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor {
	private final List<Check> checks;

	public Monitor(List<Check> checks, Level ok, Level unknown) {
		this.checks = checks;
	}

	public Map<Check, Level> check() {
		Map<Check, Level> map = new HashMap<Check, Level>();
		return map;

	}
}

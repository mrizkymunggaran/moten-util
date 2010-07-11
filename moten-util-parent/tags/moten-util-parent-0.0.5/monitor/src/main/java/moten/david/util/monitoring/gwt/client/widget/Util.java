package moten.david.util.monitoring.gwt.client.widget;

import moten.david.util.monitoring.gwt.client.check.AppCheckResult;

public class Util {
	public static AppCheckResult findResult(AppCheckResult[] results,
			String name) {
		for (AppCheckResult result : results)
			if (result.getName().equals(name))
				return result;
		return null;
	}
}

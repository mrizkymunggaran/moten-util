package moten.david.util.monitoring.gwt.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import moten.david.util.monitoring.gwt.client.ApplicationService;
import moten.david.util.monitoring.gwt.client.check.AppCheck;
import moten.david.util.monitoring.gwt.client.check.AppCheckResult;
import moten.david.util.monitoring.gwt.client.check.AppChecks;
import moten.david.util.monitoring.gwt.client.check.AppDependency;

public class ApplicationServiceDummyProvider implements
		ApplicationServiceProvider {

	@Override
	public ApplicationService get() {
		return new ApplicationService() {
			@Override
			public String getApplicationName() {
				return "application";
			}

			@Override
			public void check() {

			}

			@Override
			public AppChecks getResults() {
				AppChecks checks = new AppChecks();
				ArrayList<AppCheck> appChecks = new ArrayList<AppCheck>();
				ArrayList<AppCheckResult> results = new ArrayList<AppCheckResult>();
				for (int i = 0; i < 5; i++) {

					AppCheck check2 = createAppCheck("container available",
							"UrlAvailable(container.url)", "SEVERE",
							list("NOTIFY_ON_CALL"), new ArrayList<String>(),
							list("NOTIFY_ON_CALL", "NOTIFY_DEVELOPERS"),
							list("LOG"));
					AppCheck check = createAppCheck(
							"container manager available",
							"UrlAvailable(container.manager.url)", "SEVERE",
							list("NOTIFY_ON_CALL"), new ArrayList<String>(),
							list("NOTIFY_ON_CALL", "NOTIFY_DEVELOPERS"),
							list("LOG"), check2);
					appChecks.add(check);
					AppCheckResult result = createAppCheckResult(check
							.getName(), "SEVERE");
					AppCheckResult result2 = createAppCheckResult(check2
							.getName(), "OK");
					results.add(result);
					results.add(result2);
				}
				checks.setChecks(appChecks.toArray(new AppCheck[] {}));
				checks.setResults(results.toArray(new AppCheckResult[] {}));
				return checks;
			}

			private AppCheck createAppCheck(String name, String expression,
					String level, List<String> failurePolicies,
					List<String> unknownPolicies,
					List<String> exceptionPolicies, List<String> okPolicies,
					AppCheck... dependencies) {
				AppCheck c = new AppCheck();
				c.setName(name);
				c.setExpression(expression);
				c.setFailureLevel(level);
				c.setFailurePolicies(failurePolicies);
				c.setExceptionPolicies(exceptionPolicies);
				c.setUnknownPolicies(unknownPolicies);
				c.setOkPolicies(okPolicies);
				Set<AppDependency> set = new HashSet<AppDependency>();
				for (AppCheck dependency : dependencies)
					set.add(createAppDependency(dependency));
				c.setDependencies(set);
				return c;
			}

			private AppDependency createAppDependency(AppCheck check) {
				AppDependency d = new AppDependency();
				d.setCheck(check);
				d.setLevelInherited(true);
				return d;
			}

			public AppCheckResult createAppCheckResult(String name, String level) {
				AppCheckResult result = new AppCheckResult();
				result.setLevel(level);
				result.setInherited(true);
				result.setName(name);
				return result;
			}

			public <T> List<T> list(T... elements) {
				List<T> list = new ArrayList<T>();
				for (T t : elements)
					list.add(t);
				return list;
			}
		};
	}
}
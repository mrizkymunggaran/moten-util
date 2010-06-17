package moten.david.util.monitoring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Checker {

	private static Logger log = Logger.getLogger(Checker.class.getName());

	private final List<Check> checks;
	private final Level unknown;
	private final Level ok;
	private final Level exception;

	public Checker(Level ok, Level unknown, Level exception, List<Check> checks) {
		this.checks = checks;
		this.ok = ok;
		this.unknown = unknown;
		this.exception = exception;
	}

	public Checker(Level ok, Level unknown, Level exception, Check... checks) {
		this.ok = ok;
		this.unknown = unknown;
		this.exception = exception;
		this.checks = new ArrayList<Check>();
		for (Check check : checks)
			this.checks.add(check);
	}

	public Map<Check, CheckResult> check() {
		Map<Check, CheckResult> map = new HashMap<Check, CheckResult>();
		for (Check check : checks) {
			check(map, check);
		}
		return map;
	}

	private Handler createHandler(final StringBuffer s) {
		return new Handler() {
			private final SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.SSS");
			private final Thread thread = Thread.currentThread();

			@Override
			public void close() throws SecurityException {
			}

			@Override
			public void flush() {
			}

			@Override
			public void publish(LogRecord record) {
				if (thread == Thread.currentThread()) {
					if (s.length() > 0)
						s.append("\n");
					Date when = new Date(record.getMillis());
					s.append(sdf.format(when) + " " + record.getLoggerName()
							+ " " + record.getLevel().getName() + " - "
							+ record.getMessage());
				}
			}
		};
	}

	private CheckResult check(Map<Check, CheckResult> map, Check check) {
		StringBuffer s = new StringBuffer();
		Handler handler = createHandler(s);
		log.getParent().addHandler(handler);
		if (map.get(check) == null) {
			boolean depsOk = true;
			if (check.getDependencies() != null)
				for (Dependency dep : check.getDependencies()) {
					CheckResult result = check(map, dep.getCheck());
					if (!result.getLevel().equals(ok))
						depsOk = false;
				}
			if (!depsOk)
				map.put(check, new CheckResult(unknown));
			else {
				log.info("checking " + check.getName());
				check.getEvaluationContext().setParameters(
						check.getParameters());
				try {
					if (check.getExpression().evaluate())
						map.put(check, new CheckResult(ok));
					else
						map
								.put(check, new CheckResult(check
										.getFailureLevel()));
				} catch (RuntimeException e) {
					e.printStackTrace();
					map.put(check, new CheckResult(exception, e));
				}
			}
		}
		map.get(check).setLog(s.toString());
		log.getParent().removeHandler(handler);
		return map.get(check);
	}

	public List<Check> getChecks() {
		return checks;
	}
}

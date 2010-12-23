package moten.david.util.monitoring;

public class CheckResult {

	private final Level level;
	private final Throwable exception;
	private String log;

	public void setLog(String log) {
		this.log = log;
	}

	private final boolean inherited;

	public boolean isInherited() {
		return inherited;
	}

	public Level getLevel() {
		return level;
	}

	public Throwable getException() {
		return exception;
	}

	public CheckResult(Level level, Throwable exception, boolean inherited,
			String log) {
		super();
		this.level = level;
		this.exception = exception;
		this.inherited = inherited;
		this.log = log;
	}

	public CheckResult(Level level) {
		this(level, null, false, null);
	}

	public CheckResult(Level level, boolean inherited) {
		this(level, null, inherited, null);
	}

	public CheckResult(Level level, Throwable e) {
		this(level, e, false, null);
	}

	@Override
	public String toString() {
		return level.toString();
	}

	public String getLog() {
		return log;
	}

}

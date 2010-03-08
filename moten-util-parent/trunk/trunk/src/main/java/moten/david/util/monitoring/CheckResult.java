package moten.david.util.monitoring;

public class CheckResult {

	private final Level level;
	private final Throwable exception;
	private final boolean inherited;

	public Level getLevel() {
		return level;
	}

	public Throwable getException() {
		return exception;
	}

	public CheckResult(Level level, Throwable exception, boolean inherited) {
		super();
		this.level = level;
		this.exception = exception;
		this.inherited = inherited;
	}

	public CheckResult(Level level) {
		this(level, null, false);
	}

	public CheckResult(Level level, boolean inherited) {
		this(level, null, inherited);
	}

	public CheckResult(Level level, Throwable e) {
		this(level, e, false);
	}

	@Override
	public String toString() {
		return level.toString();
	}

}

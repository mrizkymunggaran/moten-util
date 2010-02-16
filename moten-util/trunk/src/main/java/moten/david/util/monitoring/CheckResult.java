package moten.david.util.monitoring;

public class CheckResult {

	private final Level level;
	private final Throwable exception;

	@Override
	public String toString() {
		return level.toString();
	}

	public Level getLevel() {
		return level;
	}

	public Throwable getException() {
		return exception;
	}

	public CheckResult(Level level, Throwable exception) {
		super();
		this.level = level;
		this.exception = exception;
	}

	public CheckResult(Level level) {
		this(level, null);
	}

}

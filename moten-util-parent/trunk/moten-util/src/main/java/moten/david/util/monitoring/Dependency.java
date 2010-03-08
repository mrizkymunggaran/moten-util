package moten.david.util.monitoring;

public class Dependency {

	/**
	 * The check being depended on
	 */
	private final Check check;
	/**
	 * whether a failure level of the check is passed on to the owner of the
	 * dependency
	 */
	private final boolean levelInherited;

	public Check getCheck() {
		return check;
	}

	public boolean isLevelInherited() {
		return levelInherited;
	}

	public Dependency(Check check, boolean levelInherited) {
		super();
		this.check = check;
		this.levelInherited = levelInherited;
	}

	public Dependency(Check check) {
		this(check, false);
	}

}

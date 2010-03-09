package moten.david.util.monitoring.gwt.client.check;

import java.io.Serializable;

public class AppDependency implements Serializable {

	/**
	 * The check being depended on
	 */
	private AppCheck check;
	/**
	 * whether a failure level of the check is passed on to the owner of the
	 * dependency
	 */
	private boolean levelInherited;

	public AppCheck getCheck() {
		return check;
	}

	public void setCheck(AppCheck check) {
		this.check = check;
	}

	public boolean isLevelInherited() {
		return levelInherited;
	}

	public void setLevelInherited(boolean levelInherited) {
		this.levelInherited = levelInherited;
	}

}

package moten.david.util.monitoring.gwt.server.dummy;

import moten.david.util.monitoring.Checker;
import moten.david.util.monitoring.lookup.LevelDefault;

import com.google.inject.Inject;

public class MyChecker extends Checker {

	@Inject
	public MyChecker(MyChecks checks) {
		super(LevelDefault.OK, LevelDefault.UNKNOWN, LevelDefault.EXCEPTION, checks);
	}


}

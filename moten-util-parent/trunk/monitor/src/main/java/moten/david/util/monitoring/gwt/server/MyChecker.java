package moten.david.util.monitoring.gwt.server;

import java.util.List;

import moten.david.util.monitoring.Check;
import moten.david.util.monitoring.Checker;

import com.google.inject.Inject;

public class MyChecker extends Checker {

	@Inject
	public MyChecker() {
		super(MyLevel.OK, MyLevel.UNKNOWN, MyLevel.EXCEPTION, createChecks());
	}

	private static List<Check> createChecks() {

	}

}

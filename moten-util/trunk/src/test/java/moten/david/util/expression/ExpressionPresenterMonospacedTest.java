package moten.david.util.expression;

import static moten.david.util.expression.Util.divide;
import static moten.david.util.expression.Util.minus;
import static moten.david.util.expression.Util.num;
import static moten.david.util.expression.Util.plus;
import static moten.david.util.expression.Util.times;
import static moten.david.util.monitoring.lookup.LookupType.CONFIGURATION;
import static moten.david.util.monitoring.lookup.LookupType.MONITORING;

import java.util.HashMap;
import java.util.Map;

import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.MapLookup;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionPresenterMonospacedTest {

	@Test
	public void test() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("threshold", "20");

		Map<String, String> conf = new HashMap<String, String>();
		conf.put("minimumValue", "23");
		conf.put("enabled", "false");

		MonitoringLookups lookups = new MonitoringLookups(MONITORING);
		lookups.setLookup(CONFIGURATION, new MapLookup(conf));
		lookups.setLookup(MONITORING, new MapLookup(map));
		Util.setLookups(lookups);

		ExpressionPresenter presenter = new ExpressionPresenterMonospaced();
		Assert.assertEquals("20", presenter.toString(num(20)));
		Assert.assertEquals("the.name", presenter.toString(num("the.name",
				MONITORING)));
		Assert.assertEquals("true", presenter.toString(Bool.TRUE));
		Assert.assertEquals("false", presenter.toString(Bool.FALSE));
		Assert.assertEquals("20 + 30", presenter
				.toString(plus(num(20), num(30))));
		Assert.assertEquals("20 + boo", presenter.toString(plus(num(20), num(
				"boo", MONITORING))));
		Assert.assertEquals("20 - boo", presenter.toString(minus(num(20), num(
				"boo", MONITORING))));
		Assert.assertEquals("20 * boo", presenter.toString(times(num(20), num(
				"boo", MONITORING))));
		Assert.assertEquals("20 / boo", presenter.toString(divide(num(20), num(
				"boo", MONITORING))));
		Assert.assertEquals("1 + (20 / boo)", presenter.toString(plus(num(1),
				divide(num(20), num("boo", MONITORING)))));
		Assert.assertEquals("1 + (20 / ((boo * 8) + 4))", presenter
				.toString(plus(num(1), divide(num(20), plus(times(num("boo",
						MONITORING), num(8)), num(4))))));
		// Assert.assertEquals("isNull(boo)",
		// presenter.toString(isNull("boo")));
	}
}

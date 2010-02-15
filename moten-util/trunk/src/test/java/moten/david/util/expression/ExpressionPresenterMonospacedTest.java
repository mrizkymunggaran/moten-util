package moten.david.util.expression;

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
		Expressions e = new Expressions();

		Map<String, String> map = new HashMap<String, String>();
		map.put("threshold", "20");

		Map<String, String> conf = new HashMap<String, String>();
		conf.put("minimumValue", "23");
		conf.put("enabled", "false");

		MonitoringLookups lookups = new MonitoringLookups(MONITORING);
		lookups.setLookup(CONFIGURATION, new MapLookup(conf));
		lookups.setLookup(MONITORING, new MapLookup(map));
		e.setLookups(lookups);

		ExpressionPresenter presenter = new ExpressionPresenterMonospaced();
		Assert.assertEquals("20", presenter.toString(e.num(20)));
		Assert.assertEquals("the.name", presenter.toString(e.num("the.name",
				MONITORING)));
		Assert.assertEquals("true", presenter.toString(Bool.TRUE));
		Assert.assertEquals("false", presenter.toString(Bool.FALSE));
		Assert.assertEquals("20 + 30", presenter.toString(e.plus(e.num(20), e
				.num(30))));
		Assert.assertEquals("20 + boo", presenter.toString(e.plus(e.num(20), e
				.num("boo", MONITORING))));
		Assert.assertEquals("20 - boo", presenter.toString(e.minus(e.num(20), e
				.num("boo", MONITORING))));
		Assert.assertEquals("20 * boo", presenter.toString(e.times(e.num(20), e
				.num("boo", MONITORING))));
		Assert.assertEquals("20 / boo", presenter.toString(e.divide(e.num(20),
				e.num("boo", MONITORING))));
		Assert.assertEquals("1 + (20 / boo)", presenter.toString(e.plus(e
				.num(1), e.divide(e.num(20), e.num("boo", MONITORING)))));
		Assert.assertEquals("1 + (20 / ((boo * 8) + 4))", presenter.toString(e
				.plus(e.num(1), e.divide(e.num(20), e.plus(e.times(e.num("boo",
						MONITORING), e.num(8)), e.num(4))))));
		// Assert.assertEquals("isNull(boo)",
		// presenter.toString(isNull("boo")));
	}
}

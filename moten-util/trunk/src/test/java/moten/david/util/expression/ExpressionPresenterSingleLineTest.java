package moten.david.util.expression;

import static moten.david.util.monitoring.test.MyLookupType.APPLICATION;
import static moten.david.util.monitoring.test.MyLookupType.CONFIGURATION;

import java.util.HashMap;
import java.util.Map;

import moten.david.util.monitoring.EvaluationContext;
import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.MapLookup;
import moten.david.util.monitoring.test.InjectorModule;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExpressionPresenterSingleLineTest {

	@Test
	public void test() {
		Injector injector = Guice.createInjector(new InjectorModule());
		EvaluationContext e = injector.getInstance(EvaluationContext.class);

		Map<String, String> map = new HashMap<String, String>();
		map.put("threshold", "20");

		Map<String, String> conf = new HashMap<String, String>();
		conf.put("minimumValue", "23");
		conf.put("enabled", "false");

		MonitoringLookups lookups = e.getLookups();
		lookups.put(CONFIGURATION, new MapLookup(conf));
		lookups.put(APPLICATION, new MapLookup(map));

		ExpressionPresenter presenter = new ExpressionPresenterSingleLine();
		Assert.assertEquals("20", presenter.toString(e.num(20)));
		Assert.assertEquals("APPLICATION.the.name", presenter.toString(e.num(
				"the.name", APPLICATION)));
		Assert.assertEquals("true", presenter.toString(Bool.TRUE));
		Assert.assertEquals("false", presenter.toString(Bool.FALSE));
		Assert.assertEquals("20 + 30", presenter.toString(e.plus(e.num(20), e
				.num(30))));
		Assert.assertEquals("20 + APPLICATION.boo", presenter.toString(e.plus(e
				.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 - APPLICATION.boo", presenter.toString(e.minus(
				e.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 * APPLICATION.boo", presenter.toString(e.times(
				e.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 / APPLICATION.boo", presenter.toString(e
				.divide(e.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("1 + (20 / APPLICATION.boo)", presenter
				.toString(e.plus(e.num(1), e.divide(e.num(20), e.num("boo",
						APPLICATION)))));
		Assert.assertEquals("1 + (20 / ((APPLICATION.boo * 8) + 4))", presenter
				.toString(e.plus(e.num(1), e.divide(e.num(20), e.plus(e.times(e
						.num("boo", APPLICATION), e.num(8)), e.num(4))))));
		Assert.assertEquals("20 = APPLICATION.boo", presenter.toString(e.eq(e
				.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 > APPLICATION.boo", presenter.toString(e.gt(e
				.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 < APPLICATION.boo", presenter.toString(e.lt(e
				.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 >= APPLICATION.boo", presenter.toString(e.gte(e
				.num(20), e.num("boo", APPLICATION))));
		Assert.assertEquals("20 <= APPLICATION.boo", presenter.toString(e.lte(e
				.num(20), e.num("boo", APPLICATION))));
		// Assert.assertEquals("isNull(boo)",
		// presenter.toString(isNull("boo")));
	}
}

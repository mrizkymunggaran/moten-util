package moten.david.util.monitoring.test;

import static moten.david.util.expression.Util.num;
import moten.david.util.expression.ExpressionPresenter;
import moten.david.util.expression.ExpressionPresenterMonospaced;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionPresenterMonospacedTest {

	@Test
	public void test() {
		ExpressionPresenter presenter = new ExpressionPresenterMonospaced();
		Assert.assertEquals("20", presenter.toString(num(20)));
	}
}

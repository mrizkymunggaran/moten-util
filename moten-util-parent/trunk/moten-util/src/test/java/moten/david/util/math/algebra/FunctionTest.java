package moten.david.util.math.algebra;

import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

public class FunctionTest {

	private static Logger log = Logger.getLogger(FunctionTest.class.getName());

	@Test
	public void test() {
		Variable x = var("x");
		Variable y = var("y");
		Variable z = var("z");
		Variable t = var("t");
		FunctionName gamma = new FunctionName("gamma");
		FunctionName g = new FunctionName("g");
		FunctionName mu = new FunctionName("mu");
		FunctionName nu = new FunctionName("nu");
		FunctionName intersect = new FunctionName("^", true, true, true);
		FunctionName union = new FunctionName("U", true, false, true);
		Function f = function(gamma, x, y);
		Assert.assertEquals("gamma(x,y)", f.toString());
		Assert.assertEquals("x U y", function(union, x, y).toString());

		Function prod = function(union, function(gamma, function(mu, x, y), x),
				function(g, x, y));
		System.out.println(prod.toString());
		Expression e = Util.replace(prod, x, prod);
		System.out.println(e.toString().replace(", ", ","));
		e = Util.replace(e, function(g, x, y), function(union, function(gamma,
				x, y), function(mu, x, y)));
		System.out.println(e);
		Marker a = new Marker("a");
		Marker b = new Marker("b");
		Marker c = new Marker("c");
		{
			Map<Marker, Expression> result = Util.matches(x, a);
			log.info(result + "");
			Assert.assertEquals(1, result.size());
			Assert.assertEquals(x, result.get(a));
		}
		{
			Map<Marker, Expression> result = Util.matches(x, x);
			log.info(result + "");
			Assert.assertEquals(0, result.size());
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), z), function(gamma, function(union,
					a, b), c));
			log.info(result + "");
			Assert.assertEquals(3, result.size());
			Assert.assertEquals(result.get(a), x);
			Assert.assertEquals(result.get(b), y);
			Assert.assertEquals(result.get(c), z);
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), z), function(gamma, function(union,
					a, b), a));
			log.info(result + "");
			Assert.assertNull(result);
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), function(union, y, z)), function(
					gamma, function(union, a, b), c));
			log.info(result + "");
			Assert.assertEquals(3, result.size());
			Assert.assertEquals(result.get(a), x);
			Assert.assertEquals(result.get(b), y);
			Assert.assertEquals(result.get(c), function(union, y, z));
		}
		e = function(union, e, function(mu, x, x));
		e = log(Util.replaceAll(e, function(mu, a, y), function(nu, a, y)));
		e = Util.replace(function(mu, y, y), function(mu, a, y), function(nu,
				a, y));
		Assert.assertEquals(function(nu, y, y), e);
		e = Util.replace(function(mu, x, x), function(mu, a, y), function(nu,
				a, y));
		Assert.assertEquals(function(mu, x, x), e);

		e = log(function(union, function(gamma, function(mu, x, y), x),
				function(g, x, y)));
		e = log(Util.replace(e, x, e));
		e = log(Util.replaceAll(e, function(g, a, b), function(union, function(
				gamma, a, b), function(mu, a, b))));
		e = log(Util.replace(e, function(gamma, function(union, a, b), c),
				function(union, function(gamma, a, c), function(gamma, b, c))));
	}

	private Expression log(Expression e) {
		log.info(e.toString());
		return e;
	}

	private static Variable var(String name) {
		return new Variable(name);
	}

	private static Function function(FunctionName name,
			Expression... parameters) {
		return new Function(name, parameters);
	}

}

package moten.david.util.math.algebra;

import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

public class FunctionTest {

	private static final String unionChar = "\u222A";
	private static final String intersectChar = "\u2229";
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
		FunctionName intersect = new FunctionName(intersectChar, true, true,
				true);
		FunctionName union = new FunctionName(unionChar, true, false, true);
		Function f = function(gamma, x, y);
		Assert.assertEquals("gamma(x,y)", f.toString());
		Assert.assertEquals("x " + unionChar + " y", function(union, x, y)
				.toString());

		Function prod = function(union, function(gamma, function(mu, x, y), x),
				function(g, x, y));
		Expression e = Util.replace(prod, x, prod);
		e = log(Util.replace(e, function(g, x, y), function(union, function(
				gamma, x, y), function(mu, x, y))));

		Marker a = new Marker("a");
		Marker b = new Marker("b");
		Marker c = new Marker("c");
		{
			Map<Marker, Expression> result = Util.matches(x, a);
			Assert.assertEquals(1, result.size());
			Assert.assertEquals(x, result.get(a));
		}
		{
			Map<Marker, Expression> result = Util.matches(x, x);
			Assert.assertEquals(0, result.size());
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), z), function(gamma, function(union,
					a, b), c));
			Assert.assertEquals(3, result.size());
			Assert.assertEquals(result.get(a), x);
			Assert.assertEquals(result.get(b), y);
			Assert.assertEquals(result.get(c), z);
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), z), function(gamma, function(union,
					a, b), a));
			Assert.assertNull(result);
		}
		{
			Map<Marker, Expression> result = Util.matches(function(gamma,
					function(union, x, y), function(union, y, z)), function(
					gamma, function(union, a, b), c));
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

		// real stuff
		Function exz = function(union, function(gamma, function(mu, x, z), z),
				function(g, x, z));
		e = log(function(union, function(gamma, function(mu, x, y), x),
				function(g, x, y)));
		e = log(Util.replace(exz, x, e));
		e = log(Util.replaceAll(e, function(g, a, b), function(union, function(
				gamma, a, b), function(mu, a, b))));
		e = log(Util.replaceAll(e, function(gamma, function(union, a, b), c),
				function(intersect, function(gamma, a, c),
						function(gamma, b, c))));
		e = log(Util.replaceAll(e, function(gamma, a, function(union, b, c)),
				function(union, function(gamma, a, b), function(gamma, a, c))));
		e = log(Util.replaceAll(e, function(gamma, function(mu, x, y), x), t));
		Variable gammaxy = var("gamma");
		Variable muxy = var("mu");
		e = log(Util.replaceAll(e, function(gamma, x, y), gammaxy));
		e = log(Util.replaceAll(e, function(mu, x, y), muxy));
		Variable empty = var("\u2205");
		e = log(Util.replaceAll(e, function(gamma, function(mu, a, b), b),
				empty));
		e = log(Util.replaceAll(e, function(union, empty, a), a));
		e = log(Util.replaceAll(e, function(mu, function(union, a, b), c),
				function(union, function(mu, a, c), function(mu, b, c))));

	}

	private Expression log(Expression e) {
		System.out.println(e.toString());
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

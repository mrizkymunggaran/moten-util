package moten.david.util.math.algebra;

import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

public class FunctionTest {

	private static final String unionChar = "\u222A";
	private static final String intersectChar = "\u2229";
	private static final String productChar = "\u2297";
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

		Function prod = function(union, gamma(mu(x, y), x), function(g, x, y));
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

		Assert.assertEquals(gamma(gamma(z)), replace(gamma(gamma(gamma(x))),
				gamma(x), z));
		Map<Marker, Expression> map = Util.matches(gamma(x, union(y, x)),
				gamma(a, union(b, c)));
		Assert.assertEquals(x, map.get(a));
		Assert.assertEquals(y, map.get(b));
		Assert.assertEquals(x, map.get(c));

		// real stuff
		Function exz = function(union, function(gamma, function(mu, x, z), z),
				function(g, x, z));
		e = log(function(union, function(gamma, function(mu, x, y), x),
				function(g, x, y)));
		e = log(Util.replace(exz, x, e));
		e = replaceMulti(e, function(g, a, b), function(union, function(gamma,
				a, b), function(mu, a, b)));
		e = replaceMulti(e, function(gamma, function(union, a, b), c),
				function(intersect, function(gamma, a, c),
						function(gamma, b, c)));
		e = replaceMulti(e, function(gamma, a, function(union, b, c)),
				function(union, function(gamma, a, b), function(gamma, a, c)));
		e = replaceMulti(e, function(gamma, function(mu, x, y), x), t);
		Variable gammaxy = var("gamma");
		Variable muxy = var("mu");
		e = replaceMulti(e, function(gamma, x, y), gammaxy);
		e = replaceMulti(e, function(mu, x, y), muxy);
		Variable empty = var("\u2205");
		e = replaceMulti(e, function(gamma, function(mu, a, b), b), empty);
		e = replaceMulti(e, function(union, empty, a), a);
		e = replaceMulti(e, function(mu, function(union, a, b), c), function(
				union, function(mu, a, c), function(mu, b, c)));

		// Start here for imatch analysis
		FunctionName product = new FunctionName(productChar, true, false, false);
		Function productExpansion = union(gamma(mu(x, y), x), union(
				gamma(x, y), mu(x, y)));
		log("----------");
		log(productExpansion);
		e = replace(productExpansion, y, z);
		e = replace(e, x, product(x, y));
		e = replace(e, product(a, b), union(gamma(mu(a, b), a), union(gamma(a,
				b), mu(a, b))));
		e = replace(e, gamma(union(a, b), c), intersect(gamma(a, c),
				gamma(b, c)));
		e = replace(e, gamma(a, union(b, c)), union(gamma(a, b), gamma(a, c)));
		e = replaceMulti(e, mu(a, union(b, c)), union(mu(a, b), mu(a, c)));
		e = replaceMulti(e, mu(union(a, b), c), union(mu(a, c), mu(a, c)));
		e = replaceMulti(e, gamma(union(a, b), c), intersect(gamma(a, c),
				gamma(b, c)));
		e = replaceMulti(e, gamma(a, union(b, c)), union(gamma(a, b), gamma(a,
				c)));

	}

	private Function gamma(Expression... e) {
		return createFunction("gamma", false, false, false, e);
	}

	private Function mu(Expression... e) {
		return createFunction("mu", false, false, false, e);
	}

	private Function union(Expression... e) {
		return createFunction(unionChar, true, false, true, e);
	}

	private Function product(Expression... e) {
		return createFunction(productChar, true, false, false, e);
	}

	private Function intersect(Expression... e) {
		return createFunction(intersectChar, true, true, true, e);
	}

	private Function createFunction(String name, boolean infix,
			boolean requiresBrackets, boolean commutative, Expression... e) {
		FunctionName fn = new FunctionName(name, infix, requiresBrackets,
				commutative);
		return function(fn, e);
	}

	private Expression replaceMulti(Expression e, Expression toReplace,
			Expression replaceWith) {
		log("\n    replacing all " + toReplace + " with " + replaceWith + ":\n");
		Expression result = Util.replaceAll(e, toReplace, replaceWith);
		// System.out.println(Util.toMultiline(result.toString()));
		System.out.println(result.toString());
		return result;
	}

	private Expression replace(Expression e, Expression toReplace,
			Expression replaceWith) {
		log("\n    replacing " + toReplace + " with " + replaceWith + ":\n");
		Expression result = Util.replace(e, toReplace, replaceWith);
		log(result);
		return result;
	}

	private void log(Object s) {
		System.out.println(s);
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

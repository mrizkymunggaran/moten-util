package moten.david.util.math.algebra;

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
		FunctionName gamma = new FunctionName("gamma");
		FunctionName g = new FunctionName("g");
		FunctionName mu = new FunctionName("mu");
		FunctionName intersect = new FunctionName("^", true, true);
		FunctionName union = new FunctionName("U", true, false);
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
		log.info(Util.matches(x, a) + "");
		log.info(Util.matches(function(gamma, function(union, x, y), z),
				function(gamma, function(union, a, b), c))
				+ "");
	}

	private static Variable var(String name) {
		return new Variable(name);
	}

	private static Function function(FunctionName name,
			Expression... parameters) {
		return new Function(name, parameters);
	}

}

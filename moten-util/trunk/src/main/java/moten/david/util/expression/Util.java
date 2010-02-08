package moten.david.util.expression;

import java.math.BigDecimal;

import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupProvider;

public class Util {

	public static ThreadLocal<Lookup> monitoringthreadLocal = new ThreadLocal<Lookup>();
	public static ThreadLocal<Lookup> configurationThreadLocal = new ThreadLocal<Lookup>();

	public static BooleanExpression and(BooleanExpression a, BooleanExpression b) {
		return new And(a, b);
	}

	public static BooleanExpression or(BooleanExpression a, BooleanExpression b) {
		return new Or(a, b);
	}

	public static BooleanExpression not(BooleanExpression a) {
		return new Not(a);
	}

	public static BooleanExpression eq(NumericExpression a, NumericExpression b) {
		return new Eq(a, b);
	}

	public static BooleanExpression neq(NumericExpression a, NumericExpression b) {
		return new Neq(a, b);
	}

	public static BooleanExpression gt(NumericExpression a, NumericExpression b) {
		return new Gt(a, b);
	}

	public static BooleanExpression gte(NumericExpression a, NumericExpression b) {
		return new Gte(a, b);
	}

	public static BooleanExpression lt(NumericExpression a, NumericExpression b) {
		return new Lt(a, b);
	}

	public static BooleanExpression lte(NumericExpression a, NumericExpression b) {
		return new Lte(a, b);
	}

	public static NumericExpression num(String name) {
		return new Numeric(new LookupProvider<BigDecimal>(BigDecimal.class,
				name, monitoringthreadLocal));
	}

	public static BooleanExpression isTrue(String name) {
		return new Bool(new LookupProvider<Boolean>(Boolean.class, name,
				monitoringthreadLocal));
	}

	public static NumericExpression num(long value) {
		return new Numeric(value);
	}

	public static NumericExpression num(double value) {
		return new Numeric(value);
	}

	public static BooleanExpression isNull(String name) {
		return new IsNull(new LookupProvider<String>(String.class, name,
				monitoringthreadLocal));
	}

}

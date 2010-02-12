package moten.david.util.expression;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.SingleKeyLookup;

import com.google.inject.Provider;

public class Util {

	private static MonitoringLookups lookups;

	public static void setLookups(MonitoringLookups lookups) {
		Util.lookups = lookups;
	}

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

	public static NumericExpression plus(NumericExpression a,
			NumericExpression b) {
		return new Plus(a, b);
	}

	public static NumericExpression minus(NumericExpression a,
			NumericExpression b) {
		return new Minus(a, b);
	}

	public static NumericExpression times(NumericExpression a,
			NumericExpression b) {
		return new Times(a, b);
	}

	public static NumericExpression divide(NumericExpression a,
			NumericExpression b) {
		return new Divide(a, b);
	}

	public static NumericExpression num(String name) {
		return num(name, lookups.getDefaultType());
	}

	public static NumericExpression num(String name, LookupType lookupType) {
		return new Numeric(new SingleKeyLookup<BigDecimal>(BigDecimal.class,
				name, lookups.getLookupThreadLocal(lookupType)));
	}

	public static NumericExpression num(long value) {
		return new Numeric(value);
	}

	public static NumericExpression num(double value) {
		return new Numeric(value);
	}

	public static Date date(Calendar calendar) {
		return new Date(calendar);
	}

	public static Date now() {
		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				Calendar cal = GregorianCalendar.getInstance();
				return cal;
			}
		});
	}

	public static Date date(String key) {
		return date(key, lookups.getDefaultType());
	}

	public static Date date(final String key, final LookupType type) {

		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				String value = lookups.getLookupThreadLocal(type).get()
						.get(key);
				long millis = Long.parseLong(value);
				Calendar calendar = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));
				calendar.setTimeInMillis(millis);
				return calendar;
			}
		});
	}

	public static BooleanExpression isNull(String name) {
		return isNull(name, lookups.getDefaultType());
	}

	public static BooleanExpression isNull(String name, LookupType type) {
		return new IsNull(new SingleKeyLookup<String>(String.class, name,
				lookups.getLookupThreadLocal(type)));
	}

	public static BooleanExpression isTrue(String name) {
		return isTrue(name, lookups.getDefaultType());
	}

	public static BooleanExpression isTrue(String name, LookupType type) {
		return new Bool(new SingleKeyLookup<Boolean>(Boolean.class, name,
				lookups.getLookupThreadLocal(type)));
	}

}

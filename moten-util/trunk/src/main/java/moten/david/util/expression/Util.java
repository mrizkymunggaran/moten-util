package moten.david.util.expression;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.LookupProvider;

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

	public static NumericExpression configuredNum(String name) {
		return new Numeric(new LookupProvider<BigDecimal>(BigDecimal.class,
				name, lookups.getConfigurationLookupThreadLocal()));
	}

	public static NumericExpression num(String name) {
		return new Numeric(new LookupProvider<BigDecimal>(BigDecimal.class,
				name, lookups.getMonitoringLookupThreadLocal()));
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

	public static Date date(final String key) {

		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				String value = lookups.getMonitoringLookupThreadLocal().get()
						.get(key);
				long millis = Long.parseLong(value);
				Calendar calendar = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));
				calendar.setTimeInMillis(millis);
				return calendar;
			}
		});
	}

	public static Date configuredDate(final String key) {

		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				String value = lookups.getConfigurationLookupThreadLocal()
						.get().get(key);
				long millis = Long.parseLong(value);
				Calendar calendar = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));
				calendar.setTimeInMillis(millis);
				return calendar;
			}
		});
	}

	public static BooleanExpression isNull(String name) {
		return new IsNull(new LookupProvider<String>(String.class, name,
				lookups.getMonitoringLookupThreadLocal()));
	}

	public static BooleanExpression isTrue(String name) {
		return new Bool(new LookupProvider<Boolean>(Boolean.class, name,
				lookups.getMonitoringLookupThreadLocal()));
	}

	public static BooleanExpression configuredTrue(String name) {
		return new Bool(new LookupProvider<Boolean>(Boolean.class, name,
				lookups.getConfigurationLookupThreadLocal()));
	}

}

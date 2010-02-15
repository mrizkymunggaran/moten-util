package moten.david.util.expression;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import moten.david.util.monitoring.MonitoringLookups;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.SingleKeyLookup;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class Expressions {

	private final LookupType lookupTypeDefault;
	private final MonitoringLookups lookups;

	@Inject
	public Expressions(@Named("default") LookupType lookupTypeDefault,
			MonitoringLookups lookups) {
		this.lookupTypeDefault = lookupTypeDefault;
		this.lookups = lookups;
	}

	public MonitoringLookups getLookups() {
		return lookups;
	}

	public BooleanExpression and(BooleanExpression a, BooleanExpression b) {
		return new And(a, b);
	}

	public BooleanExpression or(BooleanExpression a, BooleanExpression b) {
		return new Or(a, b);
	}

	public BooleanExpression not(BooleanExpression a) {
		return new Not(a);
	}

	public BooleanExpression eq(NumericExpression a, NumericExpression b) {
		return new Eq(a, b);
	}

	public BooleanExpression neq(NumericExpression a, NumericExpression b) {
		return new Neq(a, b);
	}

	public BooleanExpression gt(NumericExpression a, NumericExpression b) {
		return new Gt(a, b);
	}

	public BooleanExpression gte(NumericExpression a, NumericExpression b) {
		return new Gte(a, b);
	}

	public BooleanExpression lt(NumericExpression a, NumericExpression b) {
		return new Lt(a, b);
	}

	public BooleanExpression lte(NumericExpression a, NumericExpression b) {
		return new Lte(a, b);
	}

	public NumericExpression plus(NumericExpression a, NumericExpression b) {
		return new Plus(a, b);
	}

	public NumericExpression minus(NumericExpression a, NumericExpression b) {
		return new Minus(a, b);
	}

	public NumericExpression times(NumericExpression a, NumericExpression b) {
		return new Times(a, b);
	}

	public NumericExpression divide(NumericExpression a, NumericExpression b) {
		return new Divide(a, b);
	}

	public NumericExpression num(String name) {
		return num(name, this.lookupTypeDefault);
	}

	public NumericExpression num(String name, LookupType lookupType) {
		return new Numeric(new SingleKeyLookup<BigDecimal>(BigDecimal.class,
				name, lookups.get(lookupType)));
	}

	public NumericExpression num(long value) {
		return new Numeric(value);
	}

	public NumericExpression num(double value) {
		return new Numeric(value);
	}

	public Date date(Calendar calendar) {
		return new Date(calendar);
	}

	public Date now() {
		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				Calendar cal = GregorianCalendar.getInstance();
				return cal;
			}
		});
	}

	public Date date(String key) {
		return date(key, lookupTypeDefault);
	}

	public Date date(final String key, final LookupType type) {

		return new Date(new Provider<Calendar>() {
			@Override
			public Calendar get() {
				String value = lookups.get(type).get(key);
				long millis = Long.parseLong(value);
				Calendar calendar = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));
				calendar.setTimeInMillis(millis);
				return calendar;
			}
		});
	}

	public BooleanExpression isNull(String name) {
		return isNull(name, lookupTypeDefault);
	}

	public BooleanExpression isNull(String name, LookupType type) {
		return new IsNull(new SingleKeyLookup<String>(String.class, name,
				lookups.get(type)));
	}

	public BooleanExpression isTrue(String name) {
		return isTrue(name, lookupTypeDefault);
	}

	public BooleanExpression isTrue(String name, LookupType type) {
		return new Bool(new SingleKeyLookup<Boolean>(Boolean.class, name,
				lookups.get(type)));
	}

}

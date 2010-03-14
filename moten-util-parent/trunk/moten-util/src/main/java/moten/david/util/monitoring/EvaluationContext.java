package moten.david.util.monitoring;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import moten.david.util.expression.And;
import moten.david.util.expression.Bool;
import moten.david.util.expression.BooleanExpression;
import moten.david.util.expression.Date;
import moten.david.util.expression.Divide;
import moten.david.util.expression.Eq;
import moten.david.util.expression.Gt;
import moten.david.util.expression.Gte;
import moten.david.util.expression.IsNull;
import moten.david.util.expression.Lt;
import moten.david.util.expression.Lte;
import moten.david.util.expression.Minus;
import moten.david.util.expression.Neq;
import moten.david.util.expression.Not;
import moten.david.util.expression.Numeric;
import moten.david.util.expression.NumericExpression;
import moten.david.util.expression.Or;
import moten.david.util.expression.Plus;
import moten.david.util.expression.Times;
import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.LookupParameters;
import moten.david.util.monitoring.lookup.LookupType;
import moten.david.util.monitoring.lookup.Lookups;
import moten.david.util.monitoring.lookup.SingleKeyLookup;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class EvaluationContext {

	private final LookupType lookupTypeDefault;

	public LookupType getLookupTypeDefault() {
		return lookupTypeDefault;
	}

	private final Lookups lookups;
	private LookupParameters parameters;

	@Inject
	public EvaluationContext(@Named("default") LookupType lookupTypeDefault,
			Lookups lookups) {
		this.lookupTypeDefault = lookupTypeDefault;
		this.lookups = lookups;
	}

	public Lookups getLookups() {
		return lookups;
	}

	public LookupParameters getParameters() {
		return parameters;
	}

	public void setParameters(LookupParameters parameters) {
		this.parameters = parameters;
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

	private Lookup createNestedLookup(final LookupType type) {
		return new Lookup() {
			@Override
			public String get(String context, String key) {
				return lookups.get(type).get(context, key);
			}
		};
	}

	private Provider<String> createContextProvider() {
		return new Provider<String>() {

			@Override
			public String get() {
				return parameters.get(lookupTypeDefault);
			}
		};
	}

	public NumericExpression num(String key, LookupType type) {
		// use a nested lookup because lookups may not have been specified till
		// evaluate is called on the numeric expression returned by this method
		return new Numeric(new SingleKeyLookup<BigDecimal>(BigDecimal.class,
				createContextProvider(), key, createNestedLookup(type), type));
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
				String value = lookups.get(type).get(parameters.get(type), key);
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
		// use a nested lookup because lookups may not have been specified till
		// evaluate is called on the numeric expression returned by this method
		return new IsNull(new SingleKeyLookup<String>(String.class,
				createContextProvider(), name, createNestedLookup(type), type));
	}

	public BooleanExpression isTrue(String name) {
		return isTrue(name, lookupTypeDefault);
	}

	public BooleanExpression isTrue(String name, LookupType type) {
		// use a nested lookup because lookups may not have been specified till
		// evaluate is called on the numeric expression returned by this method
		return new Bool(new SingleKeyLookup<Boolean>(Boolean.class,
				createContextProvider(), name, createNestedLookup(type), type));
	}

}

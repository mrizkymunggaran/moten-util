package moten.david.util.expression;

import java.math.BigDecimal;

import moten.david.util.guice.ConstantProvider;

import com.google.inject.Provider;

public class Numeric implements NumericExpression, Provided<BigDecimal> {

	private final Provider<BigDecimal> provider;

	public Numeric(Provider<BigDecimal> provider) {
		this.provider = provider;
	}

	public Numeric(final BigDecimal value) {
		this(new ConstantProvider<BigDecimal>(value));
	}

	public Numeric(long value) {
		this(new BigDecimal(value));
	}

	public Numeric(double value) {
		this(new BigDecimal(value));
	}

	@Override
	public BigDecimal evaluate() {
		return provider.get();
	}

	@Override
	public Provider<BigDecimal> getProvider() {
		return provider;
	}

}

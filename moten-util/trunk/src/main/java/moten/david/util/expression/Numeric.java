package moten.david.util.expression;

import java.math.BigDecimal;

import com.google.inject.Provider;

public class Numeric implements NumericExpression {

	private final Provider<BigDecimal> provider;

	public Numeric(Provider<BigDecimal> provider){
		this.provider = provider;
	}
	
	public Numeric(final BigDecimal value) {
		this(new Provider<BigDecimal>(){
			@Override
			public BigDecimal get() {
				return value;
			}});
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

}

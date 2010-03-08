package moten.david.util.expression;

import java.math.BigDecimal;

import com.google.inject.Provider;

public class Duration extends Numeric {

	public Duration(final Provider<BigDecimal> provider, final DurationType type) {
		super(new Provider<BigDecimal>() {
			@Override
			public BigDecimal get() {
				return provider.get()
						.multiply(new BigDecimal(type.getFactor()));
			}
		});
	}
}

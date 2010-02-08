package moten.david.util.expression;

import java.math.BigDecimal;

import com.google.inject.Provider;

public class Duration extends Numeric {

	public Duration(Provider<BigDecimal> provider, DurationType type) {
		this.provider = provider;
	}

	@Override
	public BigDecimal evaluate() {
		return a.evaluate().add(b.evaluate());
	}

}

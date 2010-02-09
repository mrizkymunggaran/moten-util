package moten.david.util.expression;

import java.math.BigDecimal;
import java.util.Calendar;

import com.google.inject.Provider;

public class Date extends Numeric {

	public Date(final Calendar calendar) {
		this(new ConstantProvider<Calendar>(calendar));
	}

	public Date(final Provider<Calendar> provider) {
		super(new Provider<BigDecimal>() {
			@Override
			public BigDecimal get() {
				return new BigDecimal(provider.get().getTimeInMillis());
			}
		});
	}

}

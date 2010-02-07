package moten.david.util.expression;

import com.google.inject.Provider;

public class IsNull implements BooleanExpression {

	private final Provider<?> provider;

	public IsNull(Provider<?> provider) {
		this.provider = provider;
	}

	@Override
	public boolean evaluate() {
		return provider.get() == null;
	}

}

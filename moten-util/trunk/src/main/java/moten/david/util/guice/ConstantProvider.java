package moten.david.util.guice;

import com.google.inject.Provider;

public class ConstantProvider<T> implements Provider<T> {

	private final T value;

	public ConstantProvider(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

}

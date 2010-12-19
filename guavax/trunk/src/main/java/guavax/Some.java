package guavax;

import com.google.common.collect.ImmutableSet;

public class Some<T> extends SetFacade<T> implements Option<T> {
	private final T value;

	public Some(T value) {
		super(ImmutableSet.of(value));
		this.value = value;
	}

	public T get() {
		return value;
	}
}

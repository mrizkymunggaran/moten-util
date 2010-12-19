package guavax;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class None<T> extends SetFacade<T> implements Option<T> {
	public None() {
		super((Set<T>) ImmutableSet.of());
	}
}

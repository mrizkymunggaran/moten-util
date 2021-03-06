package guavax;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class None<T> extends SetFacade<T> implements Option<T> {
    @SuppressWarnings("unchecked")
    public None() {
	super((Set<T>) ImmutableSet.of());
    }
}

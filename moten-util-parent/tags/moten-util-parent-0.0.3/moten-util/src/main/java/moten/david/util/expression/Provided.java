package moten.david.util.expression;

import com.google.inject.Provider;

public interface Provided<T> {
	Provider<T> getProvider();
}

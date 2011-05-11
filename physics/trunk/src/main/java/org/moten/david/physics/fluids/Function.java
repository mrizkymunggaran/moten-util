package org.moten.david.physics.fluids;

public interface Function<T, S> {
	S apply(T position);
}

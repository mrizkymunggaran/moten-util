package org.moten.david.util.math;

public class Pair<S, T> {

	private final S a;
	private final T b;

	public Pair(S a, T b) {
		this.a = a;
		this.b = b;
	}

	public S get1() {
		return a;
	}

	public T get2() {
		return b;
	}

}

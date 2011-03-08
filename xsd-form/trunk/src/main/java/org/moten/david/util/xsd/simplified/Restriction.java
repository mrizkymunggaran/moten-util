package org.moten.david.util.xsd.simplified;

import java.util.ArrayList;
import java.util.List;

public class Restriction<T extends XsdType> {
	private final Class<T> cls;
	private final List<T> enumerations = new ArrayList<T>();
	private String pattern;

	private Restriction(Class<T> cls) {
		this.cls = cls;
	}

	public static class Builder<T extends XsdType<?>> {
		private final Restriction<T> r;

		public Builder(Class<T> cls) {
			r = new Restriction<T>(cls);
		}

		public Restriction<T> build() {
			return r;
		}

		public Builder<T> pattern(String pattern) {
			r.pattern = pattern;
			return this;
		}

		public Builder<T> enumeration(T value) {
			r.enumerations.add(value);
			return this;
		}
	}
}

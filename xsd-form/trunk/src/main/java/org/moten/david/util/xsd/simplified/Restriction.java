package org.moten.david.util.xsd.simplified;

import java.util.ArrayList;
import java.util.List;

public class Restriction {
	private final List<XsdType<?>> enumerations = new ArrayList<XsdType<?>>();
	private String pattern;

	public static class Builder {
		private final Restriction r;

		public Builder() {
			r = new Restriction();
		}

		public Restriction build() {
			return r;
		}

		public Builder pattern(String pattern) {
			r.pattern = pattern;
			return this;
		}

		public Builder enumeration(XsdType<?> value) {
			r.enumerations.add(value);
			return this;
		}
	}
}

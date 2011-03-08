package org.moten.david.util.xsd.simplified;

import javax.xml.namespace.QName;

public class Element implements Part {
	private String name;
	private int minOccurs = 1;

	public String getName() {
		return name;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public boolean isMaxUnbounded() {
		return maxUnbounded;
	}

	public QName getType() {
		return type;
	}

	private int maxOccurs = 1;
	private boolean maxUnbounded = false;
	private QName type;

	private Element() {

	}

	public static class Builder {
		private final Element e;

		public Builder() {
			e = new Element();
		}

		public Element build() {
			return e;
		}

		public Builder name(String name) {
			e.name = name;
			return this;
		}

		public Builder type(QName type) {
			e.type = type;
			return this;
		}

		public Builder minOccurs(int minOccurs) {
			e.minOccurs = minOccurs;
			return this;
		}

		public Builder maxOccurs(int maxOccurs) {
			e.maxOccurs = maxOccurs;
			return this;
		}

		public Builder maxUnbounded(boolean isUnbounded) {
			e.maxUnbounded = isUnbounded;
			return this;
		}
	}
}
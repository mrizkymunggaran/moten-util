package org.moten.david.util.xsd.simplified;

import java.io.Serializable;

public class MaxOccurs implements Serializable {
	private boolean isUnbounded;

	private Integer maxOccurs;

	public MaxOccurs() {
		this(false, 1);
	}

	public static final MaxOccurs UNBOUNDED = new MaxOccurs(true);

	public void setUnbounded(boolean isUnbounded) {
		this.isUnbounded = isUnbounded;
	}

	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	@Override
	public String toString() {
		return "MaxOccurs [isUnbounded=" + isUnbounded + ", maxOccurs="
				+ maxOccurs + "]";
	}

	public static final MaxOccurs DEFAULT = new MaxOccurs(1);

	public MaxOccurs(boolean isUnbounded) {
		this(isUnbounded, (isUnbounded ? null : 1));
	}

	public MaxOccurs(int maxOccurs) {
		this(false, maxOccurs);
	}

	private MaxOccurs(boolean isUnbounded, Integer maxOccurs) {
		this.isUnbounded = isUnbounded;
		this.maxOccurs = maxOccurs;
	}

	public boolean isUnbounded() {
		return isUnbounded;
	}

	public Integer getMaxOccurs() {
		return maxOccurs;
	}

	public static MaxOccurs parse(String s) {
		if (s == null)
			return MaxOccurs.DEFAULT;
		else if ("unbounded".equals(s))
			return MaxOccurs.UNBOUNDED;
		else
			return new MaxOccurs(Integer.parseInt(s));
	}

}

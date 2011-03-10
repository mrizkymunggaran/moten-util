package org.moten.david.util.xsd.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Restriction {
	private final List<XsdType<?>> enumerations;;
	private final String pattern;

	public Restriction(List<XsdType<?>> enumerations, String pattern,
			BigDecimal maxInclusive, BigDecimal maxExclusive,
			BigDecimal minInclusive, BigDecimal minExclusive) {
		super();
		this.enumerations = enumerations;
		this.pattern = pattern;
		this.maxInclusive = maxInclusive;
		this.maxExclusive = maxExclusive;
		this.minInclusive = minInclusive;
		this.minExclusive = minExclusive;
	}

	@Override
	public String toString() {
		return "Restriction [enumerations=" + enumerations + ", pattern="
				+ pattern + ", maxInclusive=" + maxInclusive
				+ ", maxExclusive=" + maxExclusive + ", minInclusive="
				+ minInclusive + ", minExclusive=" + minExclusive + "]";
	}

	private final BigDecimal maxInclusive;

	public List<XsdType<?>> getEnumerations() {
		return enumerations;
	}

	public String getPattern() {
		return pattern;
	}

	public BigDecimal getMaxInclusive() {
		return maxInclusive;
	}

	public BigDecimal getMaxExclusive() {
		return maxExclusive;
	}

	public BigDecimal getMinInclusive() {
		return minInclusive;
	}

	public BigDecimal getMinExclusive() {
		return minExclusive;
	}

	private final BigDecimal maxExclusive;
	private final BigDecimal minInclusive;
	private final BigDecimal minExclusive;

	public static class Builder {
		private final List<XsdType<?>> enumerations = new ArrayList<XsdType<?>>();
		private String pattern;
		private BigDecimal maxInclusive;
		private BigDecimal maxExclusive;
		private BigDecimal minInclusive;
		private BigDecimal minExclusive;

		public Builder() {
		}

		public Restriction build() {
			return new Restriction(enumerations, pattern, maxInclusive,
					maxExclusive, minInclusive, minExclusive);
		}

		public Builder pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}

		public Builder enumeration(XsdType<?> value) {
			this.enumerations.add(value);
			return this;
		}

		public void maxInclusive(BigDecimal value) {
			this.maxInclusive = value;
		}

		public void maxExclusive(BigDecimal value) {
			this.maxExclusive = value;
		}

		public void minInclusive(BigDecimal value) {
			this.minInclusive = value;
		}

		public void minExclusive(BigDecimal value) {
			this.minExclusive = value;
		}
	}
}

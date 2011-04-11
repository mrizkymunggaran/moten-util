package org.moten.david.util.xsd.simplified;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Restriction implements Serializable {
	private List<XsdType<?>> enumerations;;
	private String pattern;
	private BigDecimal maxInclusive;
	private BigDecimal maxExclusive;
	private BigDecimal minInclusive;
	private BigDecimal minExclusive;
	private QName base;

	public QName getBase() {
		return base;
	}

	public void setBase(QName base) {
		this.base = base;
	}

	public Restriction() {

	}

	public void setEnumerations(List<XsdType<?>> enumerations) {
		this.enumerations = enumerations;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setMaxInclusive(BigDecimal maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	public void setMaxExclusive(BigDecimal maxExclusive) {
		this.maxExclusive = maxExclusive;
	}

	public void setMinInclusive(BigDecimal minInclusive) {
		this.minInclusive = minInclusive;
	}

	public void setMinExclusive(BigDecimal minExclusive) {
		this.minExclusive = minExclusive;
	}

	public Restriction(List<XsdType<?>> enumerations, String pattern,
			BigDecimal maxInclusive, BigDecimal maxExclusive,
			BigDecimal minInclusive, BigDecimal minExclusive, QName base) {
		super();
		this.enumerations = enumerations;
		this.pattern = pattern;
		this.maxInclusive = maxInclusive;
		this.maxExclusive = maxExclusive;
		this.minInclusive = minInclusive;
		this.minExclusive = minExclusive;
		this.base = base;
	}

	@Override
	public String toString() {
		return "Restriction [enumerations=" + enumerations + ", pattern="
				+ pattern + ", maxInclusive=" + maxInclusive
				+ ", maxExclusive=" + maxExclusive + ", minInclusive="
				+ minInclusive + ", minExclusive=" + minExclusive + "]";
	}

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

	public static class Builder {
		private final List<XsdType<?>> enumerations = new ArrayList<XsdType<?>>();
		private String pattern;
		private BigDecimal maxInclusive;
		private BigDecimal maxExclusive;
		private BigDecimal minInclusive;
		private BigDecimal minExclusive;
		private QName base;

		public Builder() {
		}

		public Restriction build() {
			return new Restriction(enumerations, pattern, maxInclusive,
					maxExclusive, minInclusive, minExclusive, base);
		}

		public Builder base(QName base) {
			this.base = base;
			return this;
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

package moten.david.imatch.memory;

import moten.david.imatch.IdentifierType;

import com.google.inject.internal.Objects;

public class MyIdentifierType implements IdentifierType {
	private String name;
	private double strength;
	private double order;

	@Override
	public double getOrder() {
		return order;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, strength);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyIdentifierType other = (MyIdentifierType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(strength) != Double
				.doubleToLongBits(other.strength))
			return false;
		return true;
	}

	@Override
	public double getStrength() {
		return strength;
	}
}

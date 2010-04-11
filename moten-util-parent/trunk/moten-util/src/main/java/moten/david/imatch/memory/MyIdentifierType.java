package moten.david.imatch.memory;

import moten.david.imatch.IdentifierType;

import com.google.inject.internal.Objects;

public class MyIdentifierType implements IdentifierType {

	private final String name;
	private final double strength;

	public MyIdentifierType(String name, double strength) {
		super();
		this.name = name;
		this.strength = strength;
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
	public String toString() {
		return name;
	}

	@Override
	public double getStrength() {
		return strength;
	}

	public String getName() {
		return name;
	}
}

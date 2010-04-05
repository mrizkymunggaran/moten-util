package moten.david.ete.memory;

import java.io.Serializable;

import moten.david.ete.IdentifierType;

public class MyIdentifierType implements IdentifierType, Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(strength);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
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

	private final String name;

	private final double strength;

	public MyIdentifierType(String name, double strength) {
		this.name = name;
		this.strength = strength;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(IdentifierType o) {
		int result = -Double.compare(strength, ((MyIdentifierType) o).strength);
		if (result != 0)
			return result;
		else
			return name.compareTo(((MyIdentifierType) o).name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public double getStrength() {
		return strength;
	}

}

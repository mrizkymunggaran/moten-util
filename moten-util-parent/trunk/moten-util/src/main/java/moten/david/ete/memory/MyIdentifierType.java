package moten.david.ete.memory;

import java.io.Serializable;

import moten.david.ete.IdentifierType;

public class MyIdentifierType implements IdentifierType, Serializable {

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
		return Double.compare(strength, ((MyIdentifierType) o).strength);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		return true;
	}

	@Override
	public double getStrength() {
		return strength;
	}

}

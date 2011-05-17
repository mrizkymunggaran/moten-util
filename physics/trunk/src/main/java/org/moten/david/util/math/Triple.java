package org.moten.david.util.math;

public class Triple<S, T, U> {

	@Override
	public String toString() {
		return "Triple [value1=" + value1 + ", value2=" + value2 + ", value3="
				+ value3 + "]";
	}

	private final S value1;
	private final T value2;
	private final U value3;

	public Triple(S value1, T value2, U value3) {
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
		result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
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
		Triple other = (Triple) obj;
		if (value1 == null) {
			if (other.value1 != null)
				return false;
		} else if (!value1.equals(other.value1))
			return false;
		if (value2 == null) {
			if (other.value2 != null)
				return false;
		} else if (!value2.equals(other.value2))
			return false;
		if (value3 == null) {
			if (other.value3 != null)
				return false;
		} else if (!value3.equals(other.value3))
			return false;
		return true;
	}

}

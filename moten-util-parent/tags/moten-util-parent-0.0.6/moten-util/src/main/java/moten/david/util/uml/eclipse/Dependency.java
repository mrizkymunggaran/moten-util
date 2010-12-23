package moten.david.util.uml.eclipse;

/**
 * wraps a dependent class. Could we have used a ClassWrapper instead of this?
 * It doesn't seem special in any way.
 * 
 * @author dave
 * 
 */
public class Dependency {

	private final ClassWrapper classWrapper;

	public ClassWrapper getClassWrapper() {
		return classWrapper;
	}

	public Dependency(ClassWrapper classWrapper) {
		super();
		this.classWrapper = classWrapper;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classWrapper == null) ? 0 : classWrapper.hashCode());
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
		Dependency other = (Dependency) obj;
		if (classWrapper == null) {
			if (other.classWrapper != null)
				return false;
		} else if (!classWrapper.equals(other.classWrapper))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return classWrapper.toString();
	}

}

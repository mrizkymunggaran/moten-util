package moten.david.util.uml.eclipse;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ClassFilter {
	boolean accept(Class cls);

	/**
	 * Excludes java.lang.Object, Numeric and all primitive classes
	 */
	public final static ClassFilter STANDARD = new ClassFilter() {

		private final Class[] excludes = new Class[] { Object.class,
				Integer.class, Double.class, Float.class, Short.class,
				Number.class, BigDecimal.class, BigInteger.class };

		@Override
		public boolean accept(Class cls) {
			for (Class exclude : excludes)
				if (exclude.equals(cls))
					return false;
			return !cls.isPrimitive();
		}

	};

	public final static ClassFilter ACCEPT_ALL = new ClassFilter() {

		@Override
		public boolean accept(Class cls) {
			return true;
		}

	};

}

package moten.david.util.uml.eclipse;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Filters classes
 * 
 * @author dave
 * 
 */
public interface ClassFilter {

	/**
	 * returns true if the class is accepted
	 * 
	 * @param cls
	 * @return
	 */
	boolean accept(Class cls);

	/**
	 * Excludes all primitive classes, java.*, javax.* and sun.*
	 */
	public final static ClassFilter STANDARD = new ClassFilter() {

		/**
		 * classes to exclude
		 */
		private final Class[] excludes = new Class[] { Object.class,
				Integer.class, Double.class, Float.class, Short.class,
				Number.class, BigDecimal.class, BigInteger.class };

		@Override
		public boolean accept(Class cls) {
			for (Class exclude : excludes)
				if (exclude.equals(cls))
					return false;
			return !cls.isPrimitive() && !isBaseJavaClass(cls);
		}

		/**
		 * is a member of base java classes e.g. java.lang.*
		 * 
		 * @param cls
		 * @return
		 */
		private boolean isBaseJavaClass(Class cls) {
			if (cls.isArray())
				return isBaseJavaClass(cls.getComponentType());
			else
				return cls.getName().startsWith("java.")
						|| cls.getName().startsWith("javax.")
						|| cls.getName().startsWith("sun.");
		}

	};

	/**
	 * Accepts all classes
	 */
	public final static ClassFilter ACCEPT_ALL = new ClassFilter() {

		@Override
		public boolean accept(Class cls) {
			return true;
		}

	};

}

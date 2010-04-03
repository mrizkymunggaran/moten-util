package moten.david.ete;

import java.util.SortedSet;

/**
 * Utility methods for the Entity Tracking Engine.
 * 
 * @author dxm
 */
public final class Util {

	/**
	 * Returns true if and only if a and b have a common identifier.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean haveCommonIdentifier(Entity a, Entity b) {
		return haveCommonIdentifier(a.getIdentifiers().set(), b
				.getIdentifiers().set());
	}

	/**
	 * Returns true if and only if the sets have a common identifier.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean haveCommonIdentifier(
			SortedSet<? extends Identifier> a, SortedSet<? extends Identifier> b) {
		// TODO make use of the sets being sorted, this routine can be much more
		// efficient.
		for (Identifier ida : a)
			if (b.contains(ida))
				return true;
		return false;
	}

}

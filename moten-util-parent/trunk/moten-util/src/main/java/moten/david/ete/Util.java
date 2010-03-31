package moten.david.ete;

import java.util.SortedSet;

public class Util {

	public static boolean haveCommonIdentifer(Entity a, Entity b) {
		return haveCommonIdentifier(a.getIdentifiers(), b.getIdentifiers());
	}

	private static boolean haveCommonIdentifier(SortedSet<Identifier> a,
			SortedSet<Identifier> b) {
		// TODO make use of the sets being sorted, this routine can be much more
		// efficient.
		for (Identifier ida : a)
			for (Identifier idb : b)
				if (ida.equals(idb))
					return true;
		return false;
	}

}

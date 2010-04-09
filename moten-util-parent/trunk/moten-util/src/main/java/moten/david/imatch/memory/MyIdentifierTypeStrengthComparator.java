package moten.david.imatch.memory;

import moten.david.imatch.IdentifierType;
import moten.david.imatch.IdentifierTypeStrengthComparator;

public class MyIdentifierTypeStrengthComparator implements
		IdentifierTypeStrengthComparator {

	@Override
	public int compare(IdentifierType o1, IdentifierType o2) {
		return Double.compare(o1.getStrength(), o2.getStrength());
	}

}

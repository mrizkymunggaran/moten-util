package moten.david.matchstack.memory;

import moten.david.matchstack.IdentifierType;
import moten.david.matchstack.IdentifierTypeStrengthComparator;

public class MyIdentifierTypeStrengthComparator implements
		IdentifierTypeStrengthComparator {

	@Override
	public int compare(IdentifierType o1, IdentifierType o2) {
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		else
			return Double.compare(o1.getStrength(), o2.getStrength());
	}

}

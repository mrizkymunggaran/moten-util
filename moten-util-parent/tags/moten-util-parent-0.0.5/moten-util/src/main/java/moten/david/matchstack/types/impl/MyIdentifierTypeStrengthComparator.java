package moten.david.matchstack.types.impl;

import moten.david.matchstack.types.IdentifierType;
import moten.david.matchstack.types.IdentifierTypeStrengthComparator;

/**
 * Implements {@link IdentifierTypeStrengthComparator}.
 * 
 * @author dave
 * 
 */
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

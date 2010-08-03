package moten.david.matchstack.types.impl;

import java.io.Serializable;

import moten.david.matchstack.types.IdentifierType;
import moten.david.matchstack.types.IdentifierTypeStrengthComparator;

/**
 * Implements {@link IdentifierTypeStrengthComparator}.
 * 
 * @author dave
 * 
 */
public class MyIdentifierTypeStrengthComparator implements
        IdentifierTypeStrengthComparator, Serializable {

    private static final long serialVersionUID = 4504713419008790755L;

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

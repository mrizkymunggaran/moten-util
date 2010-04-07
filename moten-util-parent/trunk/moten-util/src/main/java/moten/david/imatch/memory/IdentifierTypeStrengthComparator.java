package moten.david.imatch.memory;

import java.util.Comparator;

import moten.david.imatch.IdentifierType;

public class IdentifierTypeStrengthComparator implements
        Comparator<IdentifierType> {

    @Override
    public int compare(IdentifierType o1, IdentifierType o2) {
        return Double.compare(o1.getStrength(), o2.getStrength());
    }

}

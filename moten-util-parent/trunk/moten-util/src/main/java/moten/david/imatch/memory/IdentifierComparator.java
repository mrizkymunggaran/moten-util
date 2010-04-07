package moten.david.imatch.memory;

import java.util.Comparator;

import moten.david.imatch.Identifier;

import com.google.inject.Singleton;

@Singleton
public class IdentifierComparator implements Comparator<Identifier> {

    @Override
    public int compare(Identifier o1, Identifier o2) {
        return Double.compare(o1.getIdentifierType().getOrder(), o2
                .getIdentifierType().getOrder());
    }
}

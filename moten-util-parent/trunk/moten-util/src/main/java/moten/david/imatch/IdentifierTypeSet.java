package moten.david.imatch;

import com.google.common.collect.ImmutableList;

public interface IdentifierTypeSet {
    boolean contains(IdentifierType type);

    ImmutableList<IdentifierType> list();
}

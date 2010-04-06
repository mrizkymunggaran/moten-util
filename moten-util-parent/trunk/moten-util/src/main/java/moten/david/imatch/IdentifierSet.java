package moten.david.imatch;

import com.google.common.collect.ImmutableList;

public interface IdentifierSet {

    boolean contains(Identifier identifier);

    IdentifierSet union(IdentifierSet set);

    IdentifierSet complement(IdentifierSet set);

    boolean equals(IdentifierSet set);

    boolean isEmpty();

    ImmutableList<Identifier> list();

}

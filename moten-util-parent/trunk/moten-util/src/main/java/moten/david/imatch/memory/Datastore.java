package moten.david.imatch.memory;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;

import com.google.common.collect.ImmutableSet;

public interface Datastore {
    ImmutableSet<Identifier> identifiers();

    IdentifierSet alpha(Identifier identifier);

    double time(IdentifierSet set);

    Datastore add(IdentifierSet set, double time);
}

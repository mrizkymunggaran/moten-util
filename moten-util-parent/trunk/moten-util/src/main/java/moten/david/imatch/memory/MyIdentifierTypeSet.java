package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Set;

import moten.david.imatch.IdentifierType;
import moten.david.imatch.IdentifierTypeSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class MyIdentifierTypeSet implements IdentifierTypeSet {

    private final ImmutableSet<IdentifierType> set;

    public MyIdentifierTypeSet(Set<IdentifierType> set) {
        this.set = ImmutableSet.copyOf(set);
    }

    public MyIdentifierTypeSet() {
        this(Collections.EMPTY_SET);
    }

    @Override
    public IdentifierTypeSet add(IdentifierType identifierType) {
        Builder<IdentifierType> builder = ImmutableSet.builder();
        builder.add(set.toArray(new IdentifierType[] {}));
        builder.add(identifierType);
        return new MyIdentifierTypeSet(builder.build());
    }

    @Override
    public boolean contains(IdentifierType type) {
        return set.contains(type);
    }

    @Override
    public ImmutableSet<IdentifierType> set() {
        return set;
    }

}

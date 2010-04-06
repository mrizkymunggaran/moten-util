package moten.david.imatch;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

public class MergerImpl implements Merger {
    private final IdentifierSet emptySet;

    private final Functions f;

    private final IdentitySetFactory identitySetFactory;

    @Inject
    public MergerImpl(Functions functions, IdentitySetFactory identitySetFactory) {
        this.f = functions;
        this.identitySetFactory = identitySetFactory;
        emptySet = identitySetFactory.create();
    }

    @Override
    public IdentifierSet merge(IdentifierSet a, final Identifier x) {
        if (a.contains(x)) {
            if (a.isEmpty())
                return emptySet;
            else {
                final IdentifierSet pma = f.pm(a);
                if (pma.isEmpty())
                    return a;
                else {
                    IdentifierSet nms = f.nms(a, pma);
                    if (nms.contains(x))
                        return nms;
                    else {
                        IdentifierSet alphax = f.alpha(x);
                        if (pma.equals(alphax))
                            return pma;
                        else if (f.d(f.t(x)) == f.dmax(alphax)) {
                            IdentifierSet z = calculateZ(alphax, pma, a);
                            final IdentifierTypeSet zTypes = getTypes(z);
                            return pma.union(z).complement(
                                    pma.filter(new Predicate<Identifier>() {
                                        @Override
                                        public boolean apply(Identifier i) {
                                            return zTypes.contains(f.t(i));
                                        }
                                    }));
                        } else if (f.time(a) > f.time(alphax)) {
                            return pma.add(x).complement(
                                    pma.filter(new Predicate<Identifier>() {
                                        @Override
                                        public boolean apply(Identifier i) {
                                            return f.t(i).equals(f.t(x));
                                        }
                                    }));
                        } else
                            return pma;
                    }
                }
            }
        } else {
            if (a.isEmpty())
                return emptySet;
            else {
                return f.alpha(x).complement(merge(a, a));
            }
        }
    }

    private IdentifierTypeSet getTypes(final IdentifierSet ids) {
        return new IdentifierTypeSet() {

            @Override
            public boolean contains(IdentifierType type) {
                for (Identifier id : ids.list()) {
                    if (id.getIdentifierType().equals(type))
                        return true;
                }
                return false;
            }

            @Override
            public ImmutableSet<IdentifierType> set() {
                com.google.common.collect.ImmutableSet.Builder<IdentifierType> builder = ImmutableSet
                        .builder();
                for (Identifier id : ids.list())
                    builder.add(id.getIdentifierType());
                return builder.build();
            }
        };
    }

    private IdentifierSet calculateZ(final IdentifierSet alphax,
            final IdentifierSet pma, final IdentifierSet a) {
        final IdentifierSet conflicts = f.conflicting(alphax, pma);
        return alphax.filter(new Predicate<Identifier>() {
            @Override
            public boolean apply(Identifier i) {
                return conflicts.contains(i) || f.time(alphax) < f.time(a);
            }
        });
    }

    private IdentifierSet merge(IdentifierSet a, IdentifierSet bSet) {
        IdentifierSet ids = identitySetFactory.create();
        for (Identifier b : bSet.list())
            ids = ids.union(merge(a, b));
        return ids;
    }

}

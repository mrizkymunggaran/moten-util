package moten.david.imatch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class MergerImpl implements Merger {
    private static final IdentifierSet EMPTY_SET = new EmptySet();

    private final Functions f;

    public MergerImpl(Functions functions) {
        this.f = functions;
    }

    @Override
    public IdentifierSet merge(IdentifierSet a, Identifier x) {
        if (a.contains(x)) {
            if (a.isEmpty())
                return EMPTY_SET;
            else {
                IdentifierSet pma = f.pm(a);
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
                            // TODO
                            return null;
                        } else if (f.time(a) > f.time(alphax)) {
                            // TODO
                            return null;
                        } else
                            return pma;
                    }
                }
            }
        } else {
            if (a.isEmpty())
                return EMPTY_SET;
            else {
                return f.alpha(x).complement(merge(a, a));
            }
        }
    }

    private IdentifierSet merge(IdentifierSet a, IdentifierSet b) {
        // TODO
        return null;
    }

    private static class EmptySet implements IdentifierSet {

        private final ImmutableList<Identifier> list;

        public EmptySet() {
            Builder<Identifier> builder = ImmutableList.builder();
            list = builder.build();
        }

        @Override
        public boolean contains(Identifier identifier) {
            return false;
        }

        @Override
        public boolean equals(IdentifierSet set) {
            if (set == null)
                return false;
            else
                return set.isEmpty();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ImmutableList<Identifier> list() {
            return list;
        }

        @Override
        public IdentifierSet complement(IdentifierSet set) {
            return this;
        }

        @Override
        public IdentifierSet union(IdentifierSet set) {
            return set;
        }

    };
}

package moten.david.matchstack.memory;

import static moten.david.matchstack.memory.Util.ids;
import static moten.david.matchstack.memory.Util.types;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.matchstack.Identifier;
import moten.david.matchstack.IdentifierSetStrictComparator;
import moten.david.matchstack.IdentifierType;
import moten.david.matchstack.TimedIdentifier;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Merger {

    private static Logger log = Logger.getLogger(Merger.class.getName());
    private final IdentifierSetStrictComparator strictSetComparator;

    @Inject
    public Merger(IdentifierSetStrictComparator strictSetComparator) {
        this.strictSetComparator = strictSetComparator;

    }

    private void log(String s) {
        log.fine(s);
    }

    /**
     * Returns true if and only if <code>x</code> contains an identifier used in
     * <code>y</code>
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean containsAnyTimed(Set<Identifier> x, Set<TimedIdentifier> y) {
        for (TimedIdentifier i : y)
            if (x.contains(i.getIdentifier()))
                return true;
        return false;
    }

    /**
     * Returns the primary match for x.
     * 
     * @param x
     * @return
     */
    public Set<TimedIdentifier> pm(Set<Set<TimedIdentifier>> z,
            final Set<TimedIdentifier> x) {
        final Set<Identifier> idsX = ids(x);
        log("pm filtering");
        Set<Set<TimedIdentifier>> intersecting = Functional.filter(z,
                new Predicate<Set<TimedIdentifier>>() {
                    @Override
                    public boolean apply(Set<TimedIdentifier> i) {
                        return containsAnyTimed(idsX, i);
                    }
                });
        log("pm finished filtering");
        log("calculating size");
        int size = intersecting.size();
        log("size = " + size);
        if (size == 0)
            return ImmutableSet.of();
        else {
            log("pm calculating max");
            Set<TimedIdentifier> result = Collections.max(intersecting,
                    strictSetComparator);
            log("pm calculated result");
            return result;
        }
    }

    /**
     * Returns the result of the function g on the parameters.
     * 
     * @param x
     * @param y
     * @return
     */
    public Set<TimedIdentifier> g(final Set<TimedIdentifier> x,
            final Set<TimedIdentifier> y) {
        return Sets.union(gamma(x, y), mu(x, y));
    }

    /**
     * Returns the identifier of given IdentifierType if one exists else returns
     * null.
     * 
     * @param x
     * @param identifierType
     * @return
     */
    public static TimedIdentifier getIdentifierOfType(Set<TimedIdentifier> x,
            IdentifierType identifierType) {
        for (TimedIdentifier i : x)
            if (i.getIdentifier().getIdentifierType().equals(identifierType))
                return i;
        // not found
        return null;
    }

    /**
     * Returns the product of x and y given the new set r.
     * 
     * @param x
     * @param y
     * @param r
     * @return
     */
    public Set<TimedIdentifier> product(final Set<TimedIdentifier> x,
            final Set<TimedIdentifier> y, final Set<TimedIdentifier> r) {
        if (y.size() == 0 || r.size() == 0)
            return x;
        else if (strictSetComparator.compare(r, y) < 0) {
            final Set<Identifier> yIds = ids(y);
            return Functional.filter(x, new Predicate<TimedIdentifier>() {
                @Override
                public boolean apply(TimedIdentifier i) {
                    return !yIds.contains(i);
                }
            });
        } else {
            final Set<TimedIdentifier> g = g(x, y);
            return Sets.union(gamma(g, x), g);
        }
    }

    public Set<TimedIdentifier> gamma(Set<TimedIdentifier> x,
            Set<TimedIdentifier> y) {
        final Set<IdentifierType> typesX = types(x);
        return Functional.filter(y, new Predicate<TimedIdentifier>() {
            @Override
            public boolean apply(TimedIdentifier i) {
                return !typesX.contains(i.getIdentifier().getIdentifierType());
            }
        });
    }

    public Set<TimedIdentifier> mu(final Set<TimedIdentifier> x,
            Set<TimedIdentifier> y) {
        return Functional.filter(y, new Predicate<TimedIdentifier>() {
            @Override
            public boolean apply(TimedIdentifier i) {
                TimedIdentifier id = getIdentifierOfType(x, i.getIdentifier()
                        .getIdentifierType());
                return id != null && i.getTime() > id.getTime();
            }
        });
    }

}

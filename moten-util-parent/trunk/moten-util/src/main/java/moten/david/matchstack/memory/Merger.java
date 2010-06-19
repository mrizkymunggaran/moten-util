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
import moten.david.util.functional.Fold;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The functions used for merging a new set of {@link TimedIdentifier} with a
 * set of sets of <code>TimedIdentifier</code>.
 * 
 * @author dave
 * 
 */
@Singleton
public class Merger {

    private static Logger log = Logger.getLogger(Merger.class.getName());
    private final IdentifierSetStrictComparator strictSetComparator;

    /**
     * Constructor.
     * 
     * @param strictSetComparator
     */
    @Inject
    public Merger(IdentifierSetStrictComparator strictSetComparator) {
        this.strictSetComparator = strictSetComparator;

    }

    /**
     * Logs to the class logger.
     * 
     * @param s
     */
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
    public Set<TimedIdentifier> pm(Set<Set<TimedIdentifier>> sets,
            final Set<TimedIdentifier> x) {
        final Set<Identifier> idsX = ids(x);
        log("pm filtering");
        // TODO might rejig the function definitions because sets is already the
        // intersection of z and a in all calls to this function
        // knocking this bit out would give a 1 to 2% speed improvement overall.
        Set<Set<TimedIdentifier>> intersecting = Functional.filter(sets,
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

    /**
     *Returns <code>gamma(x,y)</code>.
     * 
     * @param x
     * @param y
     * @return
     */
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

    /**
     * 
     * Returns <code>mu(x,y)</code>.
     * 
     * @param x
     * @param y
     * @return
     */
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

    /**
     * Calculates the fold of pmza with every member of intersecting in turn
     * using <code>a</code> as a reference.
     * 
     * @param pmza
     * @param a
     * @param intersecting
     * @return
     */
    public Set<TimedIdentifier> calculateFold(Set<TimedIdentifier> pmza,
            final Set<TimedIdentifier> a, Set<Set<TimedIdentifier>> intersecting) {
        return Functional.fold(intersecting,
                new Fold<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
                    @Override
                    public Set<TimedIdentifier> fold(
                            Set<TimedIdentifier> previous,
                            Set<TimedIdentifier> current) {
                        Set<TimedIdentifier> result = product(previous,
                                current, a);
                        return result;
                    }
                }, product(pmza, a, a));
    }

    /**
     * Calculates the complement.
     * 
     * @param intersecting
     * @param fold
     * @return
     */
    public Set<Set<TimedIdentifier>> calculateFoldComplement(
            final Set<Set<TimedIdentifier>> intersecting,
            Set<TimedIdentifier> fold) {
        log("calculating fold ids");
        final Set<Identifier> foldIds = ids(fold);

        return Functional.apply(intersecting,
                new Function<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
                    @Override
                    public Set<TimedIdentifier> apply(Set<TimedIdentifier> s) {
                        return Functional.filter(s,
                                new Predicate<TimedIdentifier>() {
                                    @Override
                                    public boolean apply(TimedIdentifier i) {
                                        return !foldIds.contains(i
                                                .getIdentifier());
                                    }
                                });
                    }
                });
    }

    /**
     * Merges the set of timed identifiers <code>a</code> with the entities that
     * have an intersecting identifier.
     * 
     * @param a
     *            the timed identifier set to be added.
     * @param pmza
     *            the primary match against the intersecting identifiers.
     * @param intersecting
     *            the timed identifier sets whose identifiers intersect with a.
     * @return
     */
    public Set<Set<TimedIdentifier>> merge(final Set<TimedIdentifier> a,
            Set<Set<TimedIdentifier>> intersecting) {
        Set<TimedIdentifier> pmza = pm(intersecting, a);
        log("calculating fold");
        final Set<TimedIdentifier> fold = calculateFold(pmza, a, intersecting);
        log("calculating fold complement");
        Set<Set<TimedIdentifier>> foldComplement = calculateFoldComplement(
                intersecting, fold);
        // remove the empty set if present
        return Sets.difference(Sets
                .union(foldComplement, ImmutableSet.of(fold)), ImmutableSet
                .of(ImmutableSet.of()));
    }

}

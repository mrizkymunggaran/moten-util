package moten.david.matchstack.memory;

import static moten.david.matchstack.memory.Util.ids;
import static moten.david.matchstack.memory.Util.types;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import moten.david.matchstack.Identifier;
import moten.david.matchstack.IdentifierSetStrictComparator;
import moten.david.matchstack.IdentifierType;
import moten.david.matchstack.TimedIdentifier;
import moten.david.util.functional.Fold;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Immutable Collection of timed identifier sets in combination with methods for
 * adding more identifier sets.
 * 
 * @author dave
 */
public class DatastoreImmutable {

    public static int PARTITION_SIZE = 10;

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(DatastoreImmutable.class
            .getName());

    /**
     * z contains the current set of timed identifier sets.
     */
    private final ImmutableSet<Set<TimedIdentifier>> z;
    /**
     * Strictly compares sets of identifiers.
     */
    private final IdentifierSetStrictComparator strictSetComparator;

    private final ExecutorService executorService;

    private final DatastoreImmutableFactory factory;

    /**
     * Constructor.
     * 
     * @param strictSetComparator
     * @param sets
     */
    @Inject
    public DatastoreImmutable(ExecutorService executorService,
            DatastoreImmutableFactory factory,
            IdentifierSetStrictComparator strictSetComparator,
            @Assisted Set<Set<TimedIdentifier>> sets) {
        this.executorService = executorService;
        this.factory = factory;
        this.strictSetComparator = strictSetComparator;
        Preconditions.checkNotNull(sets);
        log.info("constructor - copying sets");
        this.z = ImmutableSet.copyOf(sets);
        log.info("constructor - finished copying sets");
    }

    /**
     * Returns z.
     * 
     * @return
     */
    public ImmutableSet<Set<TimedIdentifier>> sets() {
        return z;
    }

    /**
     * Returs true if and only if <code>x</code> contains an identifier used in
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
    protected Set<TimedIdentifier> pm(final Set<TimedIdentifier> x) {
        final Set<Identifier> idsX = ids(x);
        log.info("pm filtering");
        Set<Set<TimedIdentifier>> intersecting = Functional.filter(z,
                new Predicate<Set<TimedIdentifier>>() {
                    @Override
                    public boolean apply(Set<TimedIdentifier> i) {
                        return containsAnyTimed(idsX, i);
                    }
                });
        log.info("pm finished filtering");
        log.info("calculating size");
        int size = intersecting.size();
        log.info("size = " + size);
        if (size == 0)
            return ImmutableSet.of();
        else {
            log.info("pm calculating max");
            Set<TimedIdentifier> result = Collections.max(intersecting,
                    strictSetComparator);
            log.info("pm calculated result");
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
    protected Set<TimedIdentifier> g(final Set<TimedIdentifier> x,
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
    protected static TimedIdentifier getIdentifierOfType(
            Set<TimedIdentifier> x, IdentifierType identifierType) {
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

    protected Set<TimedIdentifier> gamma(Set<TimedIdentifier> x,
            Set<TimedIdentifier> y) {
        final Set<IdentifierType> typesX = types(x);
        return Functional.filter(y, new Predicate<TimedIdentifier>() {
            @Override
            public boolean apply(TimedIdentifier i) {
                return !typesX.contains(i.getIdentifier().getIdentifierType());
            }
        });
    }

    protected Set<TimedIdentifier> mu(final Set<TimedIdentifier> x,
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
     * Returns the result of merging a new set of timed identifiers with the
     * current z.
     * 
     * @param a
     * @return
     */
    public DatastoreImmutable add(final Set<TimedIdentifier> a) {
        final Set<TimedIdentifier> pmza = pm(a);
        if (pmza.isEmpty())
            return factory.create(Sets.union(z, ImmutableSet.of(a)));
        else {
            log.info("calculating intersecting");
            final Set<Set<TimedIdentifier>> intersecting = Functional.filter(z,
                    new Predicate<Set<TimedIdentifier>>() {
                        @Override
                        public boolean apply(Set<TimedIdentifier> y) {
                            return Sets.intersection(ids(y), ids(a)).size() > 0;
                        }
                    }, executorService, PARTITION_SIZE);
            log.info("calculating fold");
            final Set<TimedIdentifier> fold = Functional.fold(intersecting,
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
            log.info("calculating fold ids");
            final Set<Identifier> foldIds = ids(fold);

            log.info("calculating fold complement");
            Set<Set<TimedIdentifier>> foldComplement = Functional.apply(z,
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
                    }, executorService, PARTITION_SIZE);
            log.info("calculating union");
            SetView<Set<TimedIdentifier>> newZ = Sets.union(foldComplement,
                    ImmutableSet.of(fold));
            // remove empty sets
            log.info("removing empty set");
            newZ = Sets.difference(newZ, ImmutableSet.of(ImmutableSet.of()));
            return factory.create(newZ);
        }
    }

    @Override
    public String toString() {
        if (z.size() == 0)
            return "empty";
        StringBuffer s = new StringBuffer();
        for (Set<TimedIdentifier> set : z) {
            if (s.length() > 0)
                s.append("\n");
            s.append(set.toString());
        }
        return s.toString();
    }
}

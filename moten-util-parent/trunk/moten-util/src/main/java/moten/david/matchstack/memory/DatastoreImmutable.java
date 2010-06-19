package moten.david.matchstack.memory;

import static moten.david.matchstack.memory.Util.ids;

import java.util.Set;
import java.util.logging.Logger;

import moten.david.matchstack.Identifier;
import moten.david.matchstack.TimedIdentifier;
import moten.david.util.collections.CollectionsUtil;
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
    private final Set<Set<TimedIdentifier>> z;

    /**
     * creates DatastoreImmutable instances.
     */
    private final DatastoreImmutableFactory factory;

    /**
     * The merger functions.
     */
    private final Merger merger;

    /**
     * Constructor.
     * 
     * @param strictSetComparator
     * @param sets
     */
    @Inject
    public DatastoreImmutable(DatastoreImmutableFactory factory, Merger merger,
            @Assisted Set<Set<TimedIdentifier>> sets) {
        this.factory = factory;
        this.merger = merger;
        Preconditions.checkNotNull(sets);
        log("constructor - copying sets");
        this.z = ImmutableSet.copyOf(sets);
        log("constructor - finished copying sets");
    }

    private void log(String s) {
        log.fine(s);
    }

    /**
     * Returns z.
     * 
     * @return
     */
    public Set<Set<TimedIdentifier>> sets() {
        return z;
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
        Set<TimedIdentifier> pmza = merger.pm(intersecting, a);
        log("calculating fold");
        final Set<TimedIdentifier> fold = Functional.fold(intersecting,
                new Fold<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
                    @Override
                    public Set<TimedIdentifier> fold(
                            Set<TimedIdentifier> previous,
                            Set<TimedIdentifier> current) {
                        Set<TimedIdentifier> result = merger.product(previous,
                                current, a);
                        return result;
                    }
                }, merger.product(pmza, a, a));
        log("calculating fold ids");
        final Set<Identifier> foldIds = ids(fold);

        log("calculating fold complement");
        Set<Set<TimedIdentifier>> foldComplement = Functional.apply(
                intersecting,
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
        // remove the empty set if present
        return Sets.difference(Sets
                .union(foldComplement, ImmutableSet.of(fold)), ImmutableSet
                .of(ImmutableSet.of()));
    }

    /**
     * Returns the result of merging a new set of timed identifiers with the
     * current z.
     * 
     * @param a
     * @return
     */
    public DatastoreImmutable add(final Set<TimedIdentifier> a) {
        final Set<Identifier> idsA = ids(a);
        Set<Set<TimedIdentifier>> intersecting = Functional.filter(z,
                new Predicate<Set<TimedIdentifier>>() {
                    @Override
                    public boolean apply(Set<TimedIdentifier> y) {
                        return CollectionsUtil.intersect(ids(y), idsA);
                    }
                });
        final Set<TimedIdentifier> pmza = merger.pm(intersecting, a);
        if (pmza.isEmpty())
            return factory.create(Sets.union(z, ImmutableSet.of(a)));
        else {
            log("calculating intersecting");

            final Set<Set<TimedIdentifier>> nonIntersecting = Sets.difference(
                    z, intersecting);

            final Set<Set<TimedIdentifier>> foldWithIntersection = merge(a,
                    intersecting);

            log("calculating union");
            SetView<Set<TimedIdentifier>> newZ = Sets.union(nonIntersecting,
                    foldWithIntersection);

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

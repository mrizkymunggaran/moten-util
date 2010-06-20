package moten.david.matchstack.datastore;

import java.util.Set;
import java.util.logging.Logger;

import moten.david.matchstack.Merger;
import moten.david.matchstack.Util;
import moten.david.matchstack.types.Identifier;
import moten.david.matchstack.types.TimedIdentifier;
import moten.david.util.collections.CollectionsUtil;
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

    /**
     * Logs the message to the class logger.
     * 
     * @param s
     */
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
     * Returns the result of merging a new set of timed identifiers with the
     * current z.
     * 
     * @param a
     * @return
     */
    public DatastoreImmutable add(final Set<TimedIdentifier> a) {

        log("calculating intersecting");
        Set<Set<TimedIdentifier>> intersecting = calculateIntersecting(z, a);

        log("calculating non-intersecting");
        final Set<Set<TimedIdentifier>> nonIntersecting = Sets.difference(z,
                intersecting);

        log("find result of merging A with intersecting");
        final Set<Set<TimedIdentifier>> foldWithIntersection = merger.merge(a,
                intersecting);

        log("calculating union");
        SetView<Set<TimedIdentifier>> newZ = Sets.union(nonIntersecting,
                foldWithIntersection);

        // return a new datastore based on newZ
        return factory.create(newZ);
    }

    /**
     * Returns those members of <code>z</
     * 
     * @param z
     * @param a
     * @return
     */
    private Set<Set<TimedIdentifier>> calculateIntersecting(
            Set<Set<TimedIdentifier>> z, Set<TimedIdentifier> a) {
        final Set<Identifier> idsA = Util.ids(a);
        return Functional.filter(z, new Predicate<Set<TimedIdentifier>>() {
            @Override
            public boolean apply(Set<TimedIdentifier> y) {
                return CollectionsUtil.intersect(Util.ids(y), idsA);
            }
        });
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

package moten.david.matchstack.memory;

import java.util.Set;

import moten.david.matchstack.Identifier;
import moten.david.matchstack.IdentifierType;
import moten.david.matchstack.TimedIdentifier;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

/**
 * Utility functions to manipulate some of the common collections of domain
 * objects.
 * 
 * @author dave
 * 
 */
public class Util {

    /**
     * Returns the Identifier set from a set of {@link TimedIdentifier}.
     * 
     * @param set
     * @return
     */
    public static Set<Identifier> ids(Set<TimedIdentifier> set) {
        return Functional.apply(set,
                new Function<TimedIdentifier, Identifier>() {
                    @Override
                    public Identifier apply(TimedIdentifier s) {
                        return s.getIdentifier();
                    }
                });
    }

    /**
     * Returns all Identifier sets from a set of TimedIdentifier sets.
     * 
     * @param sets
     * @return
     */
    public static Set<Set<Identifier>> idSets(Set<Set<TimedIdentifier>> sets) {
        return Functional.apply(sets,
                new Function<Set<TimedIdentifier>, Set<Identifier>>() {
                    @Override
                    public Set<Identifier> apply(Set<TimedIdentifier> s) {
                        return ids(s);
                    }
                });
    }

    /**
     * Returns the set of types of a set of TimedIdentifier.
     * 
     * @param set
     * @return
     */
    public static Set<IdentifierType> types(Set<TimedIdentifier> set) {
        return Functional.apply(set,
                new Function<TimedIdentifier, IdentifierType>() {
                    @Override
                    public IdentifierType apply(TimedIdentifier s) {
                        return s.getIdentifier().getIdentifierType();
                    }
                });

    }

}

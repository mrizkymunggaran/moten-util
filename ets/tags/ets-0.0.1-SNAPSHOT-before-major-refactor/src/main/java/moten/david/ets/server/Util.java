package moten.david.ets.server;

import java.util.Set;

import moten.david.ets.client.model.Identity;
import moten.david.matchstack.types.TimedIdentifier;
import moten.david.matchstack.types.impl.MyIdentifier;
import moten.david.matchstack.types.impl.MyIdentifierType;
import moten.david.matchstack.types.impl.MyTimedIdentifier;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class Util {

    /**
     * Converts an {@link Identity} to a {@link TimedIdentifier}
     */
    public static Function<? super Identity, TimedIdentifier> identityToTimedIdentifier = new Function<Identity, TimedIdentifier>() {
        @Override
        public TimedIdentifier apply(Identity i) {
            MyIdentifierType type = new MyIdentifierType(i.getName(), 1.0);
            MyIdentifier id = new MyIdentifier(type, i.getValue());
            MyTimedIdentifier ti = new MyTimedIdentifier(id, i.getTime()
                    .getTime());
            return ti;
        }
    };

    /**
     * Converts a set of {@link Identity} to a set of {@link TimedIdentifier}
     */
    public static Function<Set<Identity>, Set<TimedIdentifier>> identitySetToTimedIdentifierSet = new Function<Set<Identity>, Set<TimedIdentifier>>() {

        @Override
        public Set<TimedIdentifier> apply(Set<Identity> set) {
            return ImmutableSet.copyOf(Collections2.transform(set,
                    identityToTimedIdentifier));
        }

    };

    /**
     * Creates a set of {@link TimedIdentifier} corresponding to the identifiers
     * of <code>fix</code> with the time of <code>fix</code>.
     * 
     * @param fix
     * @return
     */
    public static Set<TimedIdentifier> createTimedIdentifierSet(MyFix fix) {
        Builder<TimedIdentifier> builder = ImmutableSet.builder();
        for (String name : fix.getIds().keySet()) {
            String value = fix.getIds().get(name);
            MyIdentifierType type = new MyIdentifierType(name, 1.0);
            MyIdentifier id = new MyIdentifier(type, value);
            MyTimedIdentifier ti = new MyTimedIdentifier(id, fix.getFix()
                    .getTime().getTime());
            builder.add(ti);
        }
        return builder.build();
    }

    /**
     * Returns the type name of a {@link TimedIdentifier} (assumes it is an
     * instance of {@link MyTimedIdentifier}.
     * 
     * @param ti
     * @return
     */
    public static String getTypeName(TimedIdentifier ti) {
        MyIdentifierType type = (MyIdentifierType) ((MyTimedIdentifier) ti)
                .getIdentifier().getIdentifierType();
        return type.getName();
    }

    /**
     * Returns the value field of a {@link TimedIdentifier} (assumes it is an
     * instance of {@link MyTimedIdentifier}.
     * 
     * @param ti
     * @return
     */
    public static String getTypeValue(TimedIdentifier ti) {
        return ((MyTimedIdentifier) ti).getIdentifier().getValue();
    }

    /**
     * Returns a string representation of the identifier part of a timed
     * identifier.
     * 
     * @param ti
     * @return
     */
    public static String getIdentityId(TimedIdentifier ti) {
        return getTypeName(ti) + ":" + getTypeValue(ti);
    }

}

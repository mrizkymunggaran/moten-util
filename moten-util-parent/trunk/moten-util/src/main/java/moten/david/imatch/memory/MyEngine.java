package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import moten.david.imatch.Engine;
import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierType;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MyEngine implements Engine {

    private final IdentifierComparator identifierComparator;
    private final IdentifierTypeStrengthComparator identifierTypeStrengthComparator;
    private final Datastore datastore;

    @Inject
    public MyEngine(IdentifierComparator identifierComparator,
            IdentifierTypeStrengthComparator identifierTypeStrengthComparator,
            @Assisted Datastore datastore) {
        this.identifierComparator = identifierComparator;
        this.identifierTypeStrengthComparator = identifierTypeStrengthComparator;
        this.datastore = datastore;
    }

    @Override
    public IdentifierSet alpha(Identifier identifier) {
        return datastore.alpha(identifier);
    }

    @Override
    public double d(IdentifierType t) {
        return t.getOrder();
    }

    @Override
    public double dmax(IdentifierSet s) {
        Identifier idmax = Collections.max(s.set(), identifierComparator);
        return idmax.getIdentifierType().getOrder();
    }

    @Override
    public IdentifierSet nms(IdentifierSet x, final IdentifierSet y) {
        return x.filter(new Predicate<Identifier>() {
            @Override
            public boolean apply(Identifier i) {
                return !y.contains(i)
                        && i.getIdentifierType().getStrength() > Collections
                                .max(y.types().set(),
                                        identifierTypeStrengthComparator)
                                .getStrength();
            }
        });
    }

    @Override
    public IdentifierSet pm(IdentifierSet ids) {
        Set<IdentifierSet> alphaX = Functional.apply(ids.set(),
                new Function<Identifier, IdentifierSet>() {
                    @Override
                    public IdentifierSet apply(Identifier s) {
                        return alpha(s);
                    }
                });
        IdentifierSet y = Collections.max(alphaX,
                new Comparator<IdentifierSet>() {
                    @Override
                    public int compare(IdentifierSet o1, IdentifierSet o2) {
                        return Double.compare(dmax(o1), dmax(o2));
                    }
                });
        return y;
    }

    @Override
    public IdentifierType t(Identifier x) {
        return x.getIdentifierType();
    }

    @Override
    public double time(IdentifierSet set) {
        return datastore.time(set);
    }

}

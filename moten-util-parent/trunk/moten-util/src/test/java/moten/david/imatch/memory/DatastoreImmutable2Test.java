package moten.david.imatch.memory;

import java.util.logging.Logger;

import junit.framework.Assert;
import moten.david.imatch.Datastore;
import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class DatastoreImmutable2Test {
    private static Logger log = Logger.getLogger(DatastoreImmutable2Test.class
            .getName());

    private final Injector injector = Guice
            .createInjector(new InjectorModule());
    @Inject
    private DatastoreImmutable2Factory factory;
    @Inject
    private IdentifierSetFactory identifierSetFactory;

    private static long millis = 0;

    @Before
    public void init() {
        injector.injectMembers(this);
    }

    @Test
    public void test() {

        ImmutableSet<IdentifierSet> a = ImmutableSet.of();
        ImmutableMap<IdentifierSet, Double> b = ImmutableMap.of();
        DatastoreImmutable2 ds = factory.create(a, b);
        log.info(ds.toString());
        ds = ds.add(createIdentifierSet("name1:boo", "name2:john"), millis);
        log.info(ds.toString());
        ds = ds.add(createIdentifierSet("name1:joe", "name2:alfie"), millis);
        log.info(ds.toString());
        ds = ds.add(createIdentifierSet("name1:joe", "name2:alf"), millis);
        log.info(ds.toString());

    }

    private void contains(Datastore ds, String... values) {
        ImmutableSet<IdentifierSet> sets = ds.identifierSets();
        IdentifierSet set = identifierSetFactory.create();
        for (String value : values)
            set = set.add(createIdentifier(value));
        Assert.assertTrue(sets.contains(set));
    }

    private IdentifierSet createIdentifierSet(String... values) {
        IdentifierSet set = identifierSetFactory.create();
        for (String value : values)
            set = set.add(createIdentifier(value));
        return set;
    }

    private Identifier createIdentifier(String value) {
        String[] items = value.split(":");
        int strength = Integer.parseInt(""
                + items[0].charAt(items[0].length() - 1));
        return createIdentifier(items[0], items[1], strength);
    }

    private Datastore add(final Datastore ds, final IdentifierSet ids) {
        Datastore ds2 = ds.add(ids, millis++);
        log.info(ds2.toString());
        return ds2;
    }

    private Identifier createIdentifier(String name, String value, int strength) {
        MyIdentifierType type = new MyIdentifierType(name, strength);
        return new MyIdentifier(type, value);
    }

    private IdentifierSet createSet() {
        IdentifierSetFactory idSetFactory = injector
                .getInstance(IdentifierSetFactory.class);
        IdentifierSet ids = idSetFactory.create();
        return ids;
    }
}

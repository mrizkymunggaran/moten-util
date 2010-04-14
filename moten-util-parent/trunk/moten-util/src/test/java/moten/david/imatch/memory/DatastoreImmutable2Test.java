package moten.david.imatch.memory;

import java.util.logging.Logger;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

import org.junit.Assert;
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
        DatastoreImmutable2 d = factory.create(a, b);
        size(d, 0);

        d = add(d, "name1:boo", "name2:john");
        size(d, 1);
        has(d, "name1:boo", "name2:john");

        d = add(d, "name1:joe", "name2:alfie");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alfie");

        d = add(d, "name1:joe", "name2:alf");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alf");

        d = add(d, "name1:joe", "name2:john");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alf");

        d = add(d, "name0:sal", "name1:joe", "name2:john");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alf");

    }

    private void has(DatastoreImmutable2 ds, String... values) {
        Assert.assertTrue(ds.sets().contains(createIdentifierSet(values)));
    }

    private void size(DatastoreImmutable2 ds, int i) {
        Assert.assertEquals(i, ds.sets().size());
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

    private DatastoreImmutable2 add(final DatastoreImmutable2 ds,
            final String... values) {
        return add(ds, createIdentifierSet(values));
    }

    private DatastoreImmutable2 add(final DatastoreImmutable2 ds,
            final IdentifierSet ids) {
        DatastoreImmutable2 ds2 = ds.add(ids, millis++);
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

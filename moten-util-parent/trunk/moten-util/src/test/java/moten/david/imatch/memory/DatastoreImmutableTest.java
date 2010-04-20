package moten.david.imatch.memory;

import java.util.Set;
import java.util.logging.Logger;

import moten.david.imatch.Identifier;
import moten.david.imatch.TimedIdentifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class DatastoreImmutableTest {
    private static Logger log = Logger.getLogger(DatastoreImmutableTest.class
            .getName());

    private final Injector injector = Guice
            .createInjector(new InjectorModule());
    @Inject
    private DatastoreImmutableFactory factory;

    private static long millis = 0;

    @Before
    public void init() {
        injector.injectMembers(this);
    }

    @Test
    public void dummy() {

    }

    @Test
    public void test() {
        ImmutableSet<Set<TimedIdentifier>> a = ImmutableSet.of();
        DatastoreImmutable d = factory.create(a);
        size(d, 0);

        d = add(d, "name1:boo", "name2:john");
        size(d, 1);
        has(d, "name1:boo", "name2:john");

        d = add(d, "name1:joe", "name2:alfie");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alfie");

        if (true)
            return;

        d = add(d, "name1:joe", "name2:alf");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alf");

        d = add(d, "name1:joe", "name2:john");
        size(d, 2);
        has(d, "name1:boo", "name2:john");
        has(d, "name1:joe", "name2:alf");
        //
        // d = add(d, "name0:sal", "name1:joe", "name2:john");
        // size(d, 2);
        // has(d, "name1:boo", "name2:john");
        // has(d, "name1:joe", "name2:alf");

    }

    private void has(DatastoreImmutable ds, String... values) {
        Assert.assertTrue(Util.idSets(ds.sets()).contains(
                createIdentifierSet(values)));
    }

    private void size(DatastoreImmutable ds, int i) {
        Assert.assertEquals(i, ds.sets().size());
    }

    private Set<Identifier> createIdentifierSet(String... values) {
        Builder<Identifier> builder = ImmutableSet.builder();
        for (String value : values)
            builder.add(createIdentifier(value));
        return builder.build();
    }

    private MyIdentifier createIdentifier(String value) {
        String[] items = value.split(":");
        int strength = Integer.parseInt(""
                + items[0].charAt(items[0].length() - 1));
        return new MyIdentifier(new MyIdentifierType(items[0], strength),
                items[1]);
    }

    private TimedIdentifier createTimedIdentifier(String value, long time) {
        return createTimedIdentifier(createIdentifier(value), time);
    }

    private TimedIdentifier createTimedIdentifier(MyIdentifier id, long time) {
        return new MyTimedIdentifier(id, time);
    }

    private DatastoreImmutable add(final DatastoreImmutable ds,
            final String... values) {
        return add(ds, createTimedIdentifierSet(millis++, values));
    }

    private Set<TimedIdentifier> createTimedIdentifierSet(long time,
            String[] values) {
        Builder<TimedIdentifier> builder = ImmutableSet.builder();
        for (String value : values)
            builder.add(createTimedIdentifier(value, time));
        return builder.build();
    }

    private DatastoreImmutable add(final DatastoreImmutable ds,
            final Set<TimedIdentifier> ids) {
        log.info("adding " + ids);
        DatastoreImmutable ds2 = ds.add(ids);
        log.info(ds2.toString());
        return ds2;
    }

    private Identifier createIdentifier(String name, String value,
            int strength, long time) {
        MyIdentifierType type = new MyIdentifierType(name, strength);
        return new MyIdentifier(type, value);
    }

}

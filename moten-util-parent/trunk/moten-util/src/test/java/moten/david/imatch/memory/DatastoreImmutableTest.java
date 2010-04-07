package moten.david.imatch.memory;

import java.util.HashMap;
import java.util.logging.Logger;

import junit.framework.Assert;
import moten.david.imatch.Datastore;
import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DatastoreImmutableTest {
    private static Logger log = Logger.getLogger(DatastoreImmutableTest.class
            .getName());

    @Test
    public void test() {
        Injector injector = Guice.createInjector(new InjectorModule());
        DatastoreImmutableFactory factory = injector
                .getInstance(DatastoreImmutableFactory.class);

        Datastore ds = factory.create(ImmutableMap
                .copyOf(new HashMap<Identifier, IdentifierSet>()), ImmutableMap
                .copyOf(new HashMap<IdentifierSet, Double>()));

        IdentifierSetFactory idSetFactory = injector
                .getInstance(IdentifierSetFactory.class);
        IdentifierSet ids = idSetFactory.create();

        MyIdentifierType name1 = new MyIdentifierType("name1", 10, 10);
        log.info(name1.toString());
        ids = ids.add(new MyIdentifier(name1, "fred"));
        log.info(ids.toString());
        ds = ds.add(ids, System.currentTimeMillis());
        log.info(ds.toString());
        Assert.assertEquals(1, ds.identifiers().size());

        // no change on readding it
        ds = ds.add(ids, System.currentTimeMillis());
        Assert.assertEquals(1, ds.identifiers().size());
    }
}

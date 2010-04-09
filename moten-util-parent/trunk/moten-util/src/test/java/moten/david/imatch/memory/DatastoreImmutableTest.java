package moten.david.imatch.memory;

import java.util.HashMap;
import java.util.logging.Logger;

import junit.framework.Assert;
import moten.david.imatch.Datastore;
import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class DatastoreImmutableTest {

	private long millis = System.currentTimeMillis();

	private static Logger log = Logger.getLogger(DatastoreImmutableTest.class
			.getName());

	private final Injector injector = Guice
			.createInjector(new InjectorModule());
	@Inject
	private DatastoreImmutableFactory factory;
	@Inject
	private IdentifierSetFactory identifierSetFactory;

	@Before
	public void init() {
		injector.injectMembers(this);
	}

	@Test
	public void test() {

		Datastore ds = factory.create(ImmutableMap
				.copyOf(new HashMap<Identifier, IdentifierSet>()), ImmutableMap
				.copyOf(new HashMap<IdentifierSet, Double>()));

		IdentifierSet ids = createSet();
		{
			ids = ids.add(createIdentifier("name1", "fred", 10, 10));
			ds = add(ds, ids);
			Assert.assertEquals(1, ds.identifiers().size());

			// no change on readding it
			ds = add(ds, ids);
			Assert.assertEquals(1, ds.identifiers().size());

			ids = createSet();
			ids = ids.add(createIdentifier("name2", "joe", 10, 11));
			ids = ids.add(createIdentifier("name4", "big guy", 10, 13));
			ds = add(ds, ids);
			Assert.assertEquals(2, ds.identifierSets().size());

			ids = createSet();
			ids = ids.add(createIdentifier("name2", "keith", 10, 11));
			ds = add(ds, ids);
			Assert.assertEquals(3, ds.identifierSets().size());

			ids = createSet();
			ids = ids.add(createIdentifier("name2", "keith", 10, 11));
			ids = ids.add(createIdentifier("name3", "john", 10, 12));
			// ds = add(ds, ids);
			// Assert.assertEquals(3, ds.identifiers().size());

		}

	}

	private IdentifierSet createIdentifierSet(String... values) {
		IdentifierSet set = identifierSetFactory.create();
		for (String value : values)
			set = set.add(createIdentifier(value));
		return set;
	}

	private Identifier createIdentifier(String value) {
		String[] items = value.split(":");
		return null;
	}

	private Datastore add(final Datastore ds, final IdentifierSet ids) {
		Datastore ds2 = ds.add(ids, millis++);
		log.info(ds2.toString());
		return ds2;
	}

	private Identifier createIdentifier(String name, String value,
			int strength, int order) {
		MyIdentifierType type = new MyIdentifierType(name, strength, order);
		return new MyIdentifier(type, value);
	}

	private IdentifierSet createSet() {
		IdentifierSetFactory idSetFactory = injector
				.getInstance(IdentifierSetFactory.class);
		IdentifierSet ids = idSetFactory.create();
		return ids;
	}
}
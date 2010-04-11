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
import com.google.common.collect.ImmutableSet;
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
			ids = ids.add(createIdentifier("name1:fred"));
			ds = add(ds, ids);
			Assert.assertEquals(1, ds.identifiers().size());
			contains(ds, "name1:fred");

			// no change on readding it
			ds = add(ds, ids);
			Assert.assertEquals(1, ds.identifiers().size());
			contains(ds, "name1:fred");

			ids = createSet();
			ids = ids.add(createIdentifier("name2:joe"));
			ids = ids.add(createIdentifier("name4:big guy"));
			ds = add(ds, ids);
			Assert.assertEquals(2, ds.identifierSets().size());
			contains(ds, "name1:fred");
			contains(ds, "name2:joe", "name4:big guy");

			ids = createSet();
			ids = ids.add(createIdentifier("name2:keith"));
			ds = add(ds, ids);
			Assert.assertEquals(3, ds.identifierSets().size());
			contains(ds, "name1:fred");
			contains(ds, "name2:joe", "name4:big guy");
			contains(ds, "name2:keith");

			ids = createSet();
			ids = ids.add(createIdentifier("name2:keith"));
			ids = ids.add(createIdentifier("name3:john"));
			ds = add(ds, ids);
			Assert.assertEquals(3, ds.identifierSets().size());
			contains(ds, "name1:fred");
			contains(ds, "name2:joe", "name4:big guy");
			contains(ds, "name2:keith", "name3:john");

			ids = createSet();
			ids = ids.add(createIdentifier("name2:keith"));
			ds = add(ds, ids);
			Assert.assertEquals(3, ds.identifierSets().size());
			contains(ds, "name1:fred");
			contains(ds, "name2:joe", "name4:big guy");
			contains(ds, "name2:keith", "name3:john");

			ids = createSet();
			ids = ids.add(createIdentifier("name0:alfred"));
			ids = ids.add(createIdentifier("name4:big guy"));
			ds = add(ds, ids);
			Assert.assertEquals(3, ds.identifierSets().size());
			contains(ds, "name1:fred");
			contains(ds, "name0:alfred", "name2:joe", "name4:big guy");
			contains(ds, "name2:keith", "name3:john");

			ids = createSet();
			ids = ids.add(createIdentifier("name0:alfie"));
			ids = ids.add(createIdentifier("name2:keith"));
			ids = ids.add(createIdentifier("name3:bert"));
			// ds = add(ds, ids);
			// Assert.assertEquals(3, ds.identifierSets().size());
		}

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
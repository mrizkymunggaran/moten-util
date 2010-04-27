package moten.david.imatch.memory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.imatch.Identifier;
import moten.david.imatch.TimedIdentifier;
import moten.david.util.text.StringUtil;
import moten.david.util.xml.TaggedOutputStream;

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

	private final List<TestInfo> tests = new ArrayList<TestInfo>();

	private static long millis = 0;

	@Before
	public void init() {
		injector.injectMembers(this);
	}

	@Test
	public void dummy() {

	}

	private static class TestInfo {
		public TestInfo(DatastoreImmutable dsBefore, Set<TimedIdentifier> a,
				DatastoreImmutable dsAfter) {
			super();
			this.dsBefore = dsBefore;
			this.a = a;
			this.dsAfter = dsAfter;
		}

		private final DatastoreImmutable dsBefore;
		private final Set<TimedIdentifier> a;
		private final DatastoreImmutable dsAfter;

		/**
		 * @return the dsBefore
		 */
		public DatastoreImmutable getDsBefore() {
			return dsBefore;
		}

		/**
		 * @return the a
		 */
		public Set<TimedIdentifier> getA() {
			return a;
		}

		/**
		 * @return the dsAfter
		 */
		public DatastoreImmutable getDsAfter() {
			return dsAfter;
		}
	}

	@Test
	public void test() throws IOException {
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

		d = add(d, "name1:joe", "name2:alf");
		size(d, 2);
		has(d, "name1:boo", "name2:john");
		has(d, "name1:joe", "name2:alf");

		d = add(d, "name1:joe", "name2:john");
		size(d, 1);
		has(d, "name1:joe", "name2:john");

		d = add(d, "name0:sal", "name1:joe", "name2:john");
		size(d, 1);
		has(d, "name0:sal", "name1:joe", "name2:john");

		d = add(d, "name0:sal", "name2:john");
		size(d, 1);
		has(d, "name0:sal", "name1:joe", "name2:john");

		d = add(d, "name1:bert", "name2:john");
		size(d, 1);
		has(d, "name0:sal", "name1:bert", "name2:john");

		d = add(d, "name3:phil");
		size(d, 2);
		has(d, "name0:sal", "name1:bert", "name2:john");
		has(d, "name3:phil");

		d = add(d, "name3:phil");
		size(d, 2);
		has(d, "name0:sal", "name1:bert", "name2:john");
		has(d, "name3:phil");

		d = add(d, "name4:logo");
		size(d, 3);
		has(d, "name0:sal", "name1:bert", "name2:john");
		has(d, "name3:phil");
		has(d, "name4:logo");

		d = add(d, "name0:sal", "name3:phil", "name4:logo");
		size(d, 1);
		has(d, "name0:sal", "name1:bert", "name2:john", "name3:phil",
				"name4:logo");

		d = add(d, "name1:fred", "name3:argy");
		size(d, 2);
		has(d, "name0:sal", "name1:bert", "name2:john", "name3:phil",
				"name4:logo");
		has(d, "name1:fred", "name3:argy");

		d = add(d, "name2:fernando", "name4:gabriel");
		size(d, 3);
		has(d, "name0:sal", "name1:bert", "name2:john", "name3:phil",
				"name4:logo");
		has(d, "name1:fred", "name3:argy");
		has(d, "name2:fernando", "name4:gabriel");

		d = add(d, "name3:argy", "name4:gabriel");
		size(d, 3);
		has(d, "name0:sal", "name1:bert", "name2:john", "name3:phil",
				"name4:logo");
		has(d, "name1:fred", "name3:argy");
		has(d, "name2:fernando", "name4:gabriel");

		{
			// merge test summary with the html docs
			String text = display(tests);
			String html = StringUtil.readString(
					new File("src/site/identifier-matching.html")).replace(
					"${tests}", text);
			FileOutputStream os = new FileOutputStream(
					"target/identifier-matching-with-tests.html");
			os.write(html.getBytes());
			os.close();
		}

		{
			FileOutputStream os = new FileOutputStream("target/style.css");
			os.write(StringUtil.readString(new File("src/site/style.css"))
					.getBytes());
			os.close();
		}

		if (true)
			return;

		for (int i = 0; i < 1000; i++) {
			int j = (int) Math.floor(Math.random() * 10);
			int v = (int) Math.floor(Math.random() * 10);
			d = add(d, "name" + j + ":value" + v);
		}

	}

	private String display(List<TestInfo> tests) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		TaggedOutputStream t = new TaggedOutputStream(bytes, true);
		t.startTag("table");
		t.addAttribute("border", "1");
		t.startTag("tr");
		t.startTag("th");
		t.append("Z");
		t.closeTag();
		t.startTag("th");
		t.append("A");
		t.closeTag();
		t.startTag("th");
		t.append("&alpha;(Z,A)");
		t.closeTag();
		t.closeTag();
		for (TestInfo test : tests) {
			t.startTag("tr");
			t.startTag("td");
			t.startTag("p");
			t.addAttribute("class", "test");
			t.append(test.getDsBefore().toString().replace("\n", "<br/>"));
			t.closeTag();
			t.closeTag();
			t.startTag("td");
			t.startTag("p");
			t.addAttribute("class", "test");
			t.append(test.getA().toString().replace("\n", "<br/>"));
			t.closeTag();
			t.closeTag();
			t.startTag("td");
			t.startTag("p");
			t.addAttribute("class", "test");
			t.append(test.getDsAfter().toString().replace("\n", "<br/>"));
			t.closeTag();
			t.closeTag();
			t.closeTag();
		}
		t.closeTag();
		t.close();
		return bytes.toString();
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
		int strength = 10 - Integer.parseInt(""
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
		tests.add(new TestInfo(ds, ids, ds2));
		log.info(ds2.toString());
		return ds2;
	}

	private Identifier createIdentifier(String name, String value,
			int strength, long time) {
		MyIdentifierType type = new MyIdentifierType(name, strength);
		return new MyIdentifier(type, value);
	}

}

package moten.david.imatch.memory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private void equals(Set<TimedIdentifier> a, Set<TimedIdentifier> b) {
		Assert.assertEquals(a, b);
	}

	private DatastoreImmutable createDatastore() {
		ImmutableSet<Set<TimedIdentifier>> a = ImmutableSet.of();
		DatastoreImmutable d = factory.create(a);
		return d;
	}

	@Test
	public void test() throws IOException {
		ImmutableSet<Set<TimedIdentifier>> a = ImmutableSet.of();
		DatastoreImmutable d = factory.create(a);
		size(d, 0);

		d = add(d, "n1:boo", "n2:john");
		size(d, 1);
		has(d, "n1:boo", "n2:john");

		d = add(d, "n1:joe", "n2:alfie");
		size(d, 2);
		has(d, "n1:boo", "n2:john");
		has(d, "n1:joe", "n2:alfie");

		d = add(d, "n1:joe", "n2:alf");
		size(d, 3);
		has(d, "n1:boo", "n2:john");
		has(d, "n1:joe", "n2:alf");
		has(d, "n2:alfie");

		d = add(d, "n1:joe", "n2:john");
		size(d, 4);
		has(d, "n1:joe", "n2:john");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");

		d = add(d, "n0:sal", "n1:joe", "n2:john");
		size(d, 4);
		has(d, "n0:sal", "n1:joe", "n2:john");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");

		d = add(d, "n0:sal", "n2:john");
		size(d, 4);
		has(d, "n0:sal", "n1:joe", "n2:john");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");

		d = add(d, "n1:bert", "n2:john");
		size(d, 5);
		has(d, "n0:sal", "n1:bert", "n2:john");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n3:phil");
		size(d, 6);
		has(d, "n0:sal", "n1:bert", "n2:john");
		has(d, "n3:phil");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n3:phil");
		size(d, 6);
		has(d, "n0:sal", "n1:bert", "n2:john");
		has(d, "n3:phil");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n4:logo");
		size(d, 7);
		has(d, "n0:sal", "n1:bert", "n2:john");
		has(d, "n3:phil");
		has(d, "n4:logo");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n0:sal", "n3:phil", "n4:logo");
		size(d, 5);
		has(d, "n0:sal", "n1:bert", "n2:john", "n3:phil", "n4:logo");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n1:fred", "n3:argy");
		size(d, 6);
		has(d, "n0:sal", "n1:bert", "n2:john", "n3:phil", "n4:logo");
		has(d, "n1:fred", "n3:argy");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n2:fernando", "n4:gabriel");
		size(d, 7);
		has(d, "n0:sal", "n1:bert", "n2:john", "n3:phil", "n4:logo");
		has(d, "n1:fred", "n3:argy");
		has(d, "n2:fernando", "n4:gabriel");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

		d = add(d, "n3:argy", "n4:gabriel");
		size(d, 7);
		has(d, "n0:sal", "n1:bert", "n2:john", "n3:phil", "n4:logo");
		has(d, "n1:fred", "n3:argy", "n4:gabriel");
		has(d, "n2:fernando");
		has(d, "n2:alf");
		has(d, "n2:alfie");
		has(d, "n1:boo");
		has(d, "n1:joe");

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

		for (int i = 0; i < 10; i++) {
			int j = (int) Math.floor(Math.random() * 10);
			int v = (int) Math.floor(Math.random() * 10);
			d = add(d, "n" + j + ":value" + v);
		}

		System.out.println(Profiler.getInstance());

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
		Pattern pattern = Pattern.compile("^.*:.*:.*$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
			return createTimedIdentifier(value);
		else
			return createTimedIdentifier(createIdentifier(value), time);
	}

	private TimedIdentifier createTimedIdentifier(String value) {
		String[] items = value.split(":");
		int strength = 10 - Integer.parseInt(""
				+ items[0].charAt(items[0].length() - 1));
		long time = millis++;
		if (items.length > 2)
			time = Integer.parseInt(items[2]);
		return new MyTimedIdentifier(new MyIdentifier(new MyIdentifierType(
				items[0], strength), items[1]), time);
	}

	private TimedIdentifier createTimedIdentifier(MyIdentifier id, long time) {
		return new MyTimedIdentifier(id, time);
	}

	private DatastoreImmutable add(final DatastoreImmutable ds,
			final String... values) {
		return add(ds, ids(values));
	}

	private Set<TimedIdentifier> ids(String... values) {
		Builder<TimedIdentifier> builder = ImmutableSet.builder();
		for (String value : values)
			builder.add(createTimedIdentifier(value));
		return builder.build();
	}

	private DatastoreImmutable add(final DatastoreImmutable ds,
			final Set<TimedIdentifier> ids) {
		log.info("adding " + ids);
		DatastoreImmutable ds2 = ds.add(ids);
		tests.add(new TestInfo(ds, ids, ds2));
		log.info("\n" + ds2.toString());
		return ds2;
	}

	private Identifier createIdentifier(String name, String value,
			int strength, long time) {
		MyIdentifierType type = new MyIdentifierType(name, strength);
		return new MyIdentifier(type, value);
	}

	@Test
	public void testProduct() {
		DatastoreImmutable d = createDatastore();
		Set<TimedIdentifier> s1;
		Set<TimedIdentifier> s2;
		Set<TimedIdentifier> s3;
		Set<TimedIdentifier> r;
		Set<TimedIdentifier> p;
		{
			s1 = ids("n1:boo:0", "n2:john:0");
			s2 = ids("n1:boo:1", "n2:fred:1");
			s3 = ids("n1:boo:1", "n2:fred:1");
			equals(s2, s3);
			p = d.product(s1, s2, s2);
			equals(s3, p);
		}
		{
			s1 = ids("n1:boo:0", "n2:john:0");
			s2 = ids("n1:boo:1", "n2:fred:1");
			s3 = ids("n1:boo:1", "n2:fred:1");
			p = d.product(s1, s2, s2);
			equals(s3, p);
		}
		{
			s1 = ids("n1:boo:0", "n2:john:0");
			s2 = ids("n1:bill:1", "n2:john:1");
			s3 = s2;
			p = d.product(s1, s2, s2);
			equals(s3, p);
		}
		{
			s1 = ids("n0:boo:0", "n2:john:0");
			s2 = ids("n1:bill:1", "n2:john:1");
			s3 = ids("n0:boo:0", "n1:bill:1", "n2:john:1");
			p = d.product(s1, s2, s2);
			equals(s3, p);
		}
		{
			s1 = ids("n0:boo:0", "n2:john:0");
			s2 = ids("n1:bill:1", "n3:hiya:1");
			s3 = ids("n2:john:3", "n3:hiya:1");
			r = ids("n0:boo:0", "n2:john:0");
			p = d.product(s1, s2, s3);
			equals(r, p);
			p = d.product(s1, s3, s3);
			equals(ids("n0:boo:0", "n2:john:3", "n3:hiya:1"));
			p = d.product(p, s2, s3);
			equals(ids("n0:boo:0", "n2:john:3", "n3:hiya:1"));
		}

	}
}

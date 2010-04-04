package moten.david.util.ete.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import moten.david.ete.Engine;
import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;
import moten.david.ete.Service;
import moten.david.ete.memory.KmlProvider;
import moten.david.ete.memory.MyEngine;
import moten.david.ete.memory.MyFix;
import moten.david.ete.memory.MyIdentifier;
import moten.david.ete.memory.MyIdentifierType;
import moten.david.ete.memory.MyPosition;
import moten.david.util.collections.CollectionsUtil;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class EngineTest {

	private static Logger log = Logger.getLogger(EngineTest.class.getName());

	private static long time = System.currentTimeMillis();

	@Test
	public void test() throws FileNotFoundException {
		Fix e = createFix("fred");
		Fix a = createFix("fred");
		Fix b = createFix("fred");
		Fix c = createFix("joe");
		Fix d = createFix("nat");
		{
			Injector injector = Guice.createInjector(new InjectorModule());
			Service fixAdder = injector.getInstance(Service.class);
			MyEngine engine = (MyEngine) injector.getInstance(Engine.class);

			fixAdder.addFix(a);
			Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));

			fixAdder.addFix(a);
			Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));
			Assert.assertEquals(a, engine.getEntities().nextElement()
					.getLatestFix());

			fixAdder.addFix(b);
			Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));
			Assert.assertEquals(b, engine.getEntities().nextElement()
					.getLatestFix());

			// add a fix before a and b, latest should still be b
			fixAdder.addFix(e);
			Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));
			Assert.assertEquals(b, engine.getEntities().nextElement()
					.getLatestFix());

			fixAdder.addFix(c);
			Assert.assertEquals(2, CollectionsUtil.count(engine.getEntities()));

			fixAdder.addFix(d);
			Assert.assertEquals(3, CollectionsUtil.count(engine.getEntities()));

			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < 100; j++)
					fixAdder.addFix(createFix("bingo" + i));
			}

			Assert.assertEquals(103, CollectionsUtil
					.count(engine.getEntities()));
			log.info("saving fixes");
			File file = new File("target/fixes.obj");
			long count = engine.saveFixes(new FileOutputStream(file));
			log.info("saved " + count + " fixes");
			log.info("file size=" + file.length() / 1024 + "K");

			KmlProvider kmlProvider = injector.getInstance(KmlProvider.class);
			kmlProvider.getKml((engine).getLatestFixes());

		}
		{
			Injector injector = Guice.createInjector(new InjectorModule());
			Service service = injector.getInstance(Service.class);
			MyEngine engine = (MyEngine) injector.getInstance(Engine.class);

			Fix f = createFix("name1:bill", "name2:bert", "name3:bart");
			Fix g = createFix("name1:art", "name2:arthur", "name3:arturo");
			Fix h = createFix("name1:joe", "name2:arthur", "name3:karl");
			Fix i = createFix("name1:alfie", "name2:arthur", "name4:johnno");
			Fix j = createFix("name1:alfie", "name2:argie", "name4:johnnosh");
			Fix k = createFix("name2:argie", "name3:brian", "name4:barry");

			service.addFix(f);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkCount(engine, 1);

			service.addFix(g);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkContains(engine, "name1:art", "name2:arthur", "name3:arturo");
			checkCount(engine, 2);

			service.addFix(h);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkContains(engine, "name1:art", "name3:arturo");
			checkContains(engine, "name1:joe", "name2:arthur", "name3:karl");
			checkCount(engine, 3);

			service.addFix(i);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkContains(engine, "name1:art", "name3:arturo");
			checkContains(engine, "name1:joe", "name3:karl");
			checkContains(engine, "name1:alfie", "name2:arthur", "name4:johnno");
			checkCount(engine, 4);

			service.addFix(j);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkContains(engine, "name1:art", "name3:arturo");
			checkContains(engine, "name1:joe", "name3:karl");
			checkContains(engine, "name1:alfie", "name2:argie",
					"name4:johnnosh");
			checkCount(engine, 4);

			service.addFix(k);
			checkContains(engine, "name1:bill", "name2:bert", "name3:bart");
			checkContains(engine, "name1:art", "name3:arturo");
			checkContains(engine, "name1:joe", "name3:karl");
			checkContains(engine, "name1:alfie", "name2:argie", "name3:brian",
					"name4:barry");
			checkCount(engine, 4);
			{
				service.addFix(createFix("name2:bert", "name3:arturo",
						"name4:barry"));

			}

			log(engine);
		}
	}

	private void checkContains(MyEngine engine, String... items) {
		Assert.assertTrue(contains(engine, items));
	}

	private void checkCount(MyEngine engine, int count) {
		Assert.assertEquals(count, Collections.list(engine.getEntities())
				.size());
	}

	private boolean contains(MyEngine engine, String... items) {
		SortedSet<MyIdentifier> itemSet = getSortedSet(items);
		Enumeration<Entity> en = engine.getEntities();
		while (en.hasMoreElements()) {
			Entity entity = en.nextElement();
			if (entity.getIdentifiers().set().equals(itemSet))
				return true;
		}
		return false;
	}

	private SortedSet<MyIdentifier> getSortedSet(String... items) {
		SortedSet<MyIdentifier> identifiers = new TreeSet<MyIdentifier>();
		int i = 0;
		for (String item : items) {
			i++;
			String[] parts = item.split(":");
			// use the last digit of the type name e.g. name4 and subtract that
			// value from 10 to get the strength
			int strength = (10 - Integer.parseInt(parts[0].substring(parts[0]
					.length() - 1)));
			MyIdentifier id = new MyIdentifier(new MyIdentifierType(parts[0],
					strength), parts[1]);
			identifiers.add(id);
		}
		return identifiers;
	}

	private void log(Engine engine) {
		StringBuffer s = new StringBuffer();
		Enumeration<Entity> en = engine.getEntities();
		while (en.hasMoreElements()) {
			s.append(en.nextElement().toString());
			s.append("\n");
		}
		log.info("state:\n" + s);
	}

	private MyFix createFix(String name) {

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time++);
		TreeSet<Identifier> ids = new TreeSet<Identifier>();
		ids.add(new MyIdentifier(new MyIdentifierType("name", 1), name));
		MyFix fix = new MyFix(ids, new MyPosition(BigDecimal.ZERO,
				BigDecimal.ZERO), calendar);
		return fix;
	}

	private MyFix createFix(String... items) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time++);
		SortedSet<MyIdentifier> ids = getSortedSet(items);
		MyFix fix = new MyFix(ids, new MyPosition(BigDecimal.ZERO,
				BigDecimal.ZERO), calendar);
		return fix;
	}
}

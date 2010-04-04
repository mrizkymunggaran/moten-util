package moten.david.util.ete.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
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

			service.addFix(f);
			service.addFix(g);
			Assert.assertEquals(2, CollectionsUtil.count(engine.getEntities()));
			service.addFix(h);
			Assert.assertEquals(3, CollectionsUtil.count(engine.getEntities()));
			service.addFix(i);
			Assert.assertEquals(4, CollectionsUtil.count(engine.getEntities()));
			log.info("state:");
			log(engine);
		}
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
		TreeSet<Identifier> ids = new TreeSet<Identifier>();
		int i = 0;
		for (String item : items) {
			i++;
			String[] parts = item.split(":");
			ids.add(new MyIdentifier(new MyIdentifierType(parts[0],
					items.length - i + 1), parts[1]));
		}
		MyFix fix = new MyFix(ids, new MyPosition(BigDecimal.ZERO,
				BigDecimal.ZERO), calendar);
		return fix;
	}
}

package moten.david.util.ete.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import moten.david.ete.Engine;
import moten.david.ete.Fix;
import moten.david.ete.FixAdder;
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
			FixAdder fixAdder = injector.getInstance(FixAdder.class);
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
				for (int j = 0; j < 1000; j++)
					fixAdder.addFix(createFix("bingo" + i));
			}

			Assert.assertEquals(103, CollectionsUtil
					.count(engine.getEntities()));
			log.info("saving fixes");
			File file = new File("target/fixes.obj");
			Assert.assertEquals(MyEngine.MAX_TOTAL_FIXES, engine
					.saveFixes(new FileOutputStream(file)));
			log.info("saved fixes");
			log.info("file size=" + file.length() / 1024 + "K");

			// KmlProvider kmlProvider =
			// injector.getInstance(KmlProvider.class);
			// log.info(kmlProvider.getKml(((MyEngine)
			// engine).getLatestFixes()));
		}
	}

	private MyFix createFix(String name) {

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time++);
		MyFix fix = new MyFix(new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO),
				calendar);
		fix.getIdentifiers().add(
				new MyIdentifier(new MyIdentifierType("name", 1), name));
		return fix;

	}
}

package moten.david.util.ete.memory;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import moten.david.ete.Engine;
import moten.david.ete.Fix;
import moten.david.ete.FixAdder;
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

    private static long time = System.currentTimeMillis();

    @Test
    public void test() {
        Fix e = createFix("fred");
        Fix a = createFix("fred");
        Fix b = createFix("fred");
        Fix c = createFix("joe");
        Fix d = createFix("nat");
        {
            Injector injector = Guice.createInjector(new InjectorModule());
            FixAdder fixAdder = injector.getInstance(FixAdder.class);
            Engine engine = injector.getInstance(Engine.class);

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

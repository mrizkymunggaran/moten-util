package moten.david.util.ete.memory;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import moten.david.ete.Engine;
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

    @Test
    public void test() {
        Injector injector = Guice.createInjector(new InjectorModule());
        FixAdder fixAdder = injector.getInstance(FixAdder.class);
        Engine engine = injector.getInstance(Engine.class);
        MyFix fix = new MyFix(new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO),
                GregorianCalendar.getInstance());
        fix.getIdentifiers().add(
                new MyIdentifier(new MyIdentifierType("name", 1), "fred"));
        fixAdder.addFix(fix);
        Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));

        fixAdder.addFix(fix);
        Assert.assertEquals(1, CollectionsUtil.count(engine.getEntities()));
    }
}

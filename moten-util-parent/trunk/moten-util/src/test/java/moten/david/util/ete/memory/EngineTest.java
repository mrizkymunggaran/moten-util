package moten.david.util.ete.memory;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import moten.david.ete.FixAdder;
import moten.david.ete.memory.MyFix;
import moten.david.ete.memory.MyPosition;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class EngineTest {

    @Test
    public void test() {
        Injector injector = Guice.createInjector(new InjectorModule());
        FixAdder fixAdder = injector.getInstance(FixAdder.class);
        MyFix fix = new MyFix(new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO),
                GregorianCalendar.getInstance());
        fixAdder.addFix(fix);
    }

}

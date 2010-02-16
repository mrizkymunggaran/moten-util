package moten.david.util.monitoring.test;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CheckGroupTest {
	@Test
	public void test() {
		Injector injector = Guice.createInjector(new InjectorModule());
		CheckGroup c = injector.getInstance(CheckGroup.class);
		c.check();
	}
}

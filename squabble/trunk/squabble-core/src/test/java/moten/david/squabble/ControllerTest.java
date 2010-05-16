package moten.david.squabble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

public class ControllerTest {

	@Test
	public void test() {
		assertEquals(list("hello", "there"), Controller.createWordFrom(list(
				"hello", "there"), "therheello"));
		assertEquals(list("a","b"), Controller.createWordFrom( list("a","b"),"ab"));
		assertEquals(list("a","b"), Controller.createWordFrom( list("a","b"),"ba"));
		assertEquals(null, Controller.createWordFrom( list("a","b"),"abc"));
		assertEquals(list("a"), Controller.createWordFrom( list("a","b"),"a"));
		assertEquals(list("b"), Controller.createWordFrom( list("a","b"),"b"));
		assertEquals(null, Controller.createWordFrom( list("a","bc"),"b"));
		assertEquals(null, Controller.createWordFrom( list("a","bb"),"b"));
		assertNull(Controller.createWordFrom( 
				list("ab", "ra", "ca", "da", "bra"), "abracadabraz"));
		System.out.println(Controller.createWordFrom(list("ab", "ra", "ca",
				"da", "bra"), "abracadabr"));
		Iterable<String> result = Controller.createWordFrom(
				list("ab", "ra", "ca", "da", "bra") , "abracadabr");
		System.out.println(result);
		assertNull(result);
	}

	private Iterable<String> list(String... values) {
		return Arrays.asList(values);
	}
}

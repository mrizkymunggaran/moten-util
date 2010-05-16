package moten.david.squabble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

public class ControllerTest {

	@Test
	public void test() {
		assertEquals(list("hello", "there"), Controller.createWordFrom(list(),list(
				"hello", "there"), "therheello"));
		assertNull(Controller.createWordFrom( list(),
				list("ab", "ra", "ca", "da", "bra"), "abracadabraz"));
		System.out.println(Controller.createWordFrom(list("ab", "ra", "ca",
				"da", "bra"), list(), "abracadabr"));
		Iterable<String> result = Controller.createWordFrom(list(),
				list("ab", "ra", "ca", "da", "bra") , "abracadabr");
		System.out.println(result);
		assertNull(result);
	}

	private Iterable<String> list(String... values) {
		return Arrays.asList(values);
	}
}

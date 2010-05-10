package moten.david.squabble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

public class ControllerTest {

	@Test
	public void test() {
		assertEquals(list("hello", "there"), Controller.createWordFrom(list(
				"hello", "there"), list(), "therheello"));
		assertNull(Controller.createWordFrom(
				list("ab", "ra", "ca", "da", "bra"), list(), "abracadabraz"));
		System.out.println(Controller.createWordFrom(list("ab", "ra", "ca",
				"da", "bra"), list(), "abracadabr"));
		assertNull(Controller.createWordFrom(
				list("ab", "ra", "ca", "da", "bra"), list(), "abracadabr"));
	}

	private Iterable<String> list(String... values) {
		return Arrays.asList(values);
	}
}

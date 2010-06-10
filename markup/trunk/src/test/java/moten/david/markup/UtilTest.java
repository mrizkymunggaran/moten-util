package moten.david.markup;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testIntersect() {
		assertTrue(Util.intersect(100, 200, 100, 200, 0));
		assertTrue(Util.intersect(100, 200, 110, 210, 0));
		assertTrue(Util.intersect(100, 200, 200, 210, 0));
		assertFalse(Util.intersect(100, 200, 210, 220, 0));
		assertFalse(Util.intersect(100, 200, 210, 220, 5));
		assertTrue(Util.intersect(100, 200, 210, 220, 15));
		assertTrue(Util.intersect(100, 200, 210, 220, 40));

		assertTrue(Util.intersect(110, 210, 100, 200, 0));
		assertTrue(Util.intersect(200, 210, 100, 200, 0));
		assertFalse(Util.intersect(210, 220, 100, 200, 0));
		assertFalse(Util.intersect(210, 220, 100, 200, 5));
		assertTrue(Util.intersect(210, 220, 100, 200, 15));
		assertTrue(Util.intersect(210, 220, 100, 200, 50));

	}
}

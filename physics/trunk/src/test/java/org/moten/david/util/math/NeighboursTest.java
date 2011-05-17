package org.moten.david.util.math;

import static org.junit.Assert.assertEquals;
import static org.moten.david.util.math.Vector.vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class NeighboursTest {

	@Test
	public void testNoEntriesReturnsNoNeighbours() {
		Set<Vector> set = Collections.EMPTY_SET;
		assertEquals(0, Neighbours.findNeighbours(set).size());
	}

	@Test
	public void testOneEntryReturnsNoNeighbours() {
		Set<Vector> set = new HashSet<Vector>();
		set.add(vector(0, 0, 0));
		assertEquals(0, Neighbours.findNeighbours(set).size());
	}

	@Test
	public void testLookups() {
		Set<Vector> set = new HashSet<Vector>();
		set.add(Vector.ORIGIN);
		set.add(vector(1, 0, 0));
		HashMap<Vector, TreeSet<Double>> lookups = Neighbours.getLookups(set);
		System.out.println("lookups=" + lookups);
		assertEquals(new TreeSet<Double>() {
			{
				add(0.0);
				add(1.0);
			}
		}, lookups.get(Neighbours.vectorRepresentativeIgnoring(Vector.ORIGIN,
				Direction.X)));

	}

	@Test
	public void testTwoEntriesReturnsTwoNeighbours() {
		Set<Vector> set = new HashSet<Vector>();
		set.add(Vector.ORIGIN);
		set.add(vector(1, 0, 0));
		Map<Triple<Vector, Direction, Boolean>, Vector> n = Neighbours
				.findNeighbours(set);
		System.out.println(n);
		assertEquals(2, n.size());
	}

}

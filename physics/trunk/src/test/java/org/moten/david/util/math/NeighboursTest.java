package org.moten.david.util.math;

import static org.junit.Assert.assertEquals;
import static org.moten.david.util.math.Vector.vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class NeighboursTest {

	@Test
	public void testNoEntriesReturnsNoNeighbours() {
		Set<Vector> set = Collections.emptySet();
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

	@Test
	public void testThreeEntriesInALineReturnsFourNeighbours() {
		Set<Vector> set = new HashSet<Vector>();
		set.add(Vector.ORIGIN);
		set.add(vector(1, 0, 0));
		set.add(vector(2, 0, 0));
		Map<Triple<Vector, Direction, Boolean>, Vector> n = Neighbours
				.findNeighbours(set);
		System.out.println(n);
		assertEquals(4, n.size());
	}

	@Test
	public void testManyEntriesReturns4NeighboursForAPointInside() {
		Set<Vector> set = new HashSet<Vector>();
		for (int i = 1; i <= 3; i++)
			for (int j = 1; j <= 3; j++)
				set.add(vector(i, j, 0));
		Map<Triple<Vector, Direction, Boolean>, Vector> n = Neighbours
				.findNeighbours(set);
		System.out.println(n);
		assertEquals(24, n.size());
		assertEquals(vector(2, 3, 0),
				n.get(Triple.triple(vector(2, 2, 0), Direction.Y, true)));
	}

	@Test
	public void testManyManyEntriesReturnsInAcceptableTime() {
		long t = System.currentTimeMillis();
		Set<Vector> set = new HashSet<Vector>();
		final int N = 5;
		for (int i = 1; i <= 100; i++)
			for (int j = 1; j <= 20; j++)
				for (int k = 1; k <= N; k++)
					set.add(vector(i, j, k));
		Map<Triple<Vector, Direction, Boolean>, Vector> n = Neighbours
				.findNeighbours(set);

		t = System.currentTimeMillis() - t;
		System.out.println("time=" + (t / 1000.0) + "s");
		double projectedTimeSeconds = (1116000 / n.size() * t / 1000);
		System.out.println("time projected for 200x200x5 is "
				+ projectedTimeSeconds + "s");
		assertEquals(54800, n.size());
		Assert.assertTrue(projectedTimeSeconds < 60);
	}
}

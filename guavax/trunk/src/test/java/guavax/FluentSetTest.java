package guavax;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

public class FluentSetTest {

    @Test
    public void test() {
	Set<Set<Integer>> s = ImmutableSet.of(
		(Set<Integer>) ImmutableSet.of(1), ImmutableSet.of(3),
		ImmutableSet.of(5, 7));
	FluentSet<Set<Integer>> set = new FluentSet<Set<Integer>>(s);
	FluentSet<Integer> ints = set
		.flatten(FluentSet.identity(Integer.class));
	ImmutableSet<Integer> ex = ImmutableSet.of(1, 3, 5, 7);
	Assert.assertTrue(ex.containsAll(ints) && ints.containsAll(ex));
    }

    @Test
    public void testFind() {

	Set<Integer> s = ImmutableSet.of(1, 3, 5, 7);
	FluentSet<Integer> set = new FluentSet<Integer>(s);
	Assert
		.assertEquals(ImmutableSet.of(3), set.find(Predicates
			.equalTo(3)));
    }

    @Test
    public void testFlatMap() {
	Set<Integer> s = ImmutableSet.of(1, 3, 5, 7);
	Set<Integer> t = ImmutableSet.of(2, 4, 6, 8);

    }
}

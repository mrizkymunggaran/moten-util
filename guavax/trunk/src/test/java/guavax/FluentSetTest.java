package guavax;

import java.util.Set;

import org.junit.Test;

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
	}
}

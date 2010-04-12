package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierTypeStrictComparator;
import moten.david.util.functional.Fold;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DatastoreImmutable2 {

	public final ImmutableSet<IdentifierSet> z;
	private final IdentifierTypeStrictComparator strictComparator;

	@Inject
	public DatastoreImmutable2(IdentifierTypeStrictComparator strictComparator,
			@Assisted Set<IdentifierSet> sets) {
		this.strictComparator = strictComparator;
		this.z = ImmutableSet.copyOf(sets);
	}

	private IdentifierSet c(final IdentifierSet x, final IdentifierSet y) {
		return x.filter(new Predicate<Identifier>() {
			@Override
			public boolean apply(Identifier i) {
				return y.types().set().contains(i.getIdentifierType())
						&& !y.contains(i);
			}
		});
	}

	private IdentifierSet pm(final IdentifierSet x) {
		if (z.size() == 0)
			return MyIdentifierSet.EMPTY_SET;
		else {
			Boolean noIntersectInZ = Functional.fold(z,
					new Fold<IdentifierSet, Boolean>() {

						@Override
						public Boolean fold(Boolean lastValue, IdentifierSet t) {
							return lastValue
									|| Sets.intersection(x.set(), t.set())
											.size() == 0;
						}

					}, false);
			if (noIntersectInZ)
				return MyIdentifierSet.EMPTY_SET;
			else {
				Collections.max(Sets.filter(z, new Predicate<IdentifierSet>() {

					@Override
					public boolean apply(IdentifierSet i) {
						return Sets.intersection(x.set(), i.set()).size() > 0;
					}
				}), new Comparator<IdentifierSet>() {

					@Override
					public int compare(IdentifierSet o1, IdentifierSet o2) {
						// TODO
						return 0;
					}

				});
			}
			return null;
		}
	}
}

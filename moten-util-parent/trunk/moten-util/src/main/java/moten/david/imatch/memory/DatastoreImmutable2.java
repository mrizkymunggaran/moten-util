package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Set;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetStrictComparator;
import moten.david.imatch.IdentifierTypeSet;
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
	private final IdentifierTypeStrictComparator strictTypeComparator;
	private final IdentifierSetStrictComparator strictSetComparator;

	@Inject
	public DatastoreImmutable2(
			IdentifierTypeStrictComparator strictTypeComparator,
			IdentifierSetStrictComparator strictSetComparator,
			@Assisted Set<IdentifierSet> sets) {
		this.strictTypeComparator = strictTypeComparator;
		this.strictSetComparator = strictSetComparator;
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
				IdentifierSet y = Collections.max(Sets.filter(z,
						new Predicate<IdentifierSet>() {
							@Override
							public boolean apply(IdentifierSet i) {
								return Sets.intersection(x.set(), i.set())
										.size() > 0;
							}
						}), strictSetComparator);
				return y;
			}
		}
	}

	private IdentifierSet g(final IdentifierSet x, final IdentifierSet y) {
		final boolean isLater = time(x) > time(y);
		return y.filter(new Predicate<Identifier>() {
			@Override
			public boolean apply(Identifier i) {
				return !x.types().contains(i.getIdentifierType()) || isLater;
			}

		});
	}

	private IdentifierSet m(final IdentifierSet x, final IdentifierSet y) {
		if (strictSetComparator.compare(x, y) < 0)
			return MyIdentifierSet.EMPTY_SET;
		else {
			final IdentifierSet g = g(x, y);
			final IdentifierTypeSet gTypes = g.types();
			IdentifierSet a = x.filter(new Predicate<Identifier>() {
				@Override
				public boolean apply(Identifier i) {
					return !gTypes.contains(i.getIdentifierType());
				}
			});
			return a.union(g);
		}
	}

	private double time(IdentifierSet set) {
		return 0;
	}

	// private DatastoreImmutable2 merge(final IdentifierSet a) {
	// IdentifierSet pmza = pm(a);
	// if (pmza.isEmpty())
	// return new DatastoreImmutable2(strictTypeComparator,
	// strictSetComparator, Sets.union(z, ImmutableSet.of(a)));
	// else
	// return Functional.fold(z.filter(new Predicate<IdentifierSet>() {
	// @Override
	// public boolean apply(IdentifierSet y) {
	// return Sets.intersect(y.set(), a.set()).size() > 0;
	// }
	// }), new Fold<IdentifierSet, IdentifierSet>() {
	//
	// @Override
	// public IdentifierSet fold(IdentifierSet lastValue,
	// IdentifierSet t) {
	// }
	// }, null);
	// }
}

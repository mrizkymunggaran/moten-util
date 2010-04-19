package moten.david.imatch.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import moten.david.imatch.IdentifierSetStrictComparator;
import moten.david.imatch.IdentifierTypeStrictComparator;
import moten.david.imatch.TimedIdentifier;
import moten.david.util.functional.Fold;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DatastoreImmutable {

	public final ImmutableSet<ImmutableSet<TimedIdentifier>> z;
	private final IdentifierTypeStrictComparator strictTypeComparator;
	private final IdentifierSetStrictComparator strictSetComparator;

	@Inject
	public DatastoreImmutable(
			IdentifierTypeStrictComparator strictTypeComparator,
			IdentifierSetStrictComparator strictSetComparator,
			@Assisted Set<ImmutableSet<TimedIdentifier>> sets) {
		this.strictTypeComparator = strictTypeComparator;
		this.strictSetComparator = strictSetComparator;
		if (sets == null)
			this.z = ImmutableSet.of();
		else
			this.z = ImmutableSet.copyOf(sets);
	}

	// private IdentifierSet c(final IdentifierSet x, final IdentifierSet y) {
	// return x.filter(new Predicate<Identifier>() {
	// @Override
	// public boolean apply(Identifier i) {
	// return y.types().set().contains(i.getIdentifierType())
	// && !y.contains(i);
	// }
	// });
	// }

	public ImmutableSet<ImmutableSet<TimedIdentifier>> sets() {
		return z;
	}

	private ImmutableSet<TimedIdentifier> empty() {
		ImmutableSet<TimedIdentifier> set = ImmutableSet.of();
		return set;
	}

	private ImmutableSet<TimedIdentifier> pm(final Set<TimedIdentifier> x) {
		if (z.size() == 0)
			return empty();
		else {
			Boolean noIntersectInZ = Functional.fold(z,
					new Fold<TimedIdentifierSet, Boolean>() {
						@Override
						public Boolean fold(Boolean lastValue,
								TimedIdentifierSet t) {
							return lastValue
									&& Sets.intersection(x.ids().set(),
											t.ids().set()).size() == 0;
						}

					}, true);
			if (noIntersectInZ)
				return MyTimedIdentifierSet.EMPTY_SET;
			else {
				TimedIdentifierSet y = Collections.max(Sets.filter(z,
						new Predicate<TimedIdentifierSet>() {
							@Override
							public boolean apply(TimedIdentifierSet i) {
								return Sets.intersection(x.ids().set(),
										i.ids().set()).size() > 0;
							}
						}), strictSetComparator);
				return y;
			}
		}
	}

	private TimedIdentifierSet g(final TimedIdentifierSet x,
			final TimedIdentifierSet y) {
		final Double maxTimeX = null;
		return y.filter(new Predicate<TimedIdentifier>() {
			@Override
			public boolean apply(TimedIdentifier i) {
				return !x.types().contains(i.getIdentifierType())
						|| maxTimeX == null || i.getTime() > maxTimeX;
			}

		});
	}

	private Double maxTime(Collection<Double> values) {
		if (values == null || values.size() == 0)
			return null;
		else
			return Collections.max(values);
	}

	private TimedIdentifierSet m(final TimedIdentifierSet x,
			final TimedIdentifierSet y) {
		if (strictSetComparator.compare(x.ids(), y.ids()) < 0)
			return MyTimedIdentifierSet.EMPTY_SET;
		else {
			final TimedIdentifierSet g = g(x, y);
			final IdentifierTypeSet gTypes = g.ids().types();
			TimedIdentifierSet a = x.filter(new Predicate<TimedIdentifier>() {
				@Override
				public boolean apply(TimedIdentifier i) {
					return !gTypes.contains(i.getIdentifierType());
				}
			});
			return a.union(g);
		}
	}

	public DatastoreImmutable add(final ImmutableSet<TimedIdentifier> a) {
		ImmutableSet<TimedIdentifier> pmza = pm(a);
		if (pmza.isEmpty())
			return new DatastoreImmutable(strictTypeComparator,
					strictSetComparator, Sets.union(z, ImmutableSet.of(a)));
		else {
			Set<TimedIdentifierSet> intersecting = Sets.filter(z,
					new Predicate<TimedIdentifierSet>() {
						@Override
						public boolean apply(TimedIdentifierSet y) {
							return Sets.intersection(y.ids().set(),
									a.ids().set()).size() > 0;
						}
					});
			TimedIdentifierSet fold = Functional.fold(intersecting,
					new Fold<TimedIdentifierSet, TimedIdentifierSet>() {
						@Override
						public TimedIdentifierSet fold(
								TimedIdentifierSet previous,
								TimedIdentifierSet current) {
							return m(previous, current);
						}
					}, m(pmza, a));
			SetView<TimedIdentifierSet> newZ = Sets.union(Sets.difference(z,
					intersecting), ImmutableSet.of(fold));
			return new DatastoreImmutable(strictTypeComparator,
					strictSetComparator, newZ);
		}
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("DataStoreImmutable=");
		for (TimedIdentifierSet set : z)
			s.append("\n" + set.toString());
		return s.toString();

	}
}

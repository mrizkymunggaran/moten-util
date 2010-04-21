package moten.david.imatch.memory;

import static moten.david.imatch.memory.Util.ids;
import static moten.david.imatch.memory.Util.types;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSetStrictComparator;
import moten.david.imatch.IdentifierType;
import moten.david.imatch.IdentifierTypeStrictComparator;
import moten.david.imatch.TimedIdentifier;
import moten.david.util.functional.Fold;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DatastoreImmutable {

	private static Logger log = Logger.getLogger(DatastoreImmutable.class
			.getName());

	public final ImmutableSet<Set<TimedIdentifier>> z;
	private final IdentifierTypeStrictComparator strictTypeComparator;
	private final IdentifierSetStrictComparator strictSetComparator;

	@Inject
	public DatastoreImmutable(
			IdentifierTypeStrictComparator strictTypeComparator,
			IdentifierSetStrictComparator strictSetComparator,
			@Assisted Set<Set<TimedIdentifier>> sets) {
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

	public ImmutableSet<Set<TimedIdentifier>> sets() {
		return z;
	}

	private ImmutableSet<TimedIdentifier> empty() {
		ImmutableSet<TimedIdentifier> set = ImmutableSet.of();
		return set;
	}

	private Set<TimedIdentifier> pm(final Set<TimedIdentifier> x) {
		if (z.size() == 0)
			return empty();
		else {
			Boolean noIntersectInZ = Functional.fold(z,
					new Fold<Set<TimedIdentifier>, Boolean>() {
						@Override
						public Boolean fold(Boolean lastValue,
								Set<TimedIdentifier> t) {
							return lastValue
									&& Sets.intersection(ids(x), ids(t)).size() == 0;
						}

					}, true);
			if (noIntersectInZ)
				return Collections.EMPTY_SET;
			else {
				Set<TimedIdentifier> y = Collections.max(Sets.filter(z,
						new Predicate<Set<TimedIdentifier>>() {
							@Override
							public boolean apply(Set<TimedIdentifier> i) {
								return Sets.intersection(ids(x), ids(i)).size() > 0;
							}
						}), strictSetComparator);
				return y;
			}
		}
	}

	private Set<TimedIdentifier> g(final Set<TimedIdentifier> x,
			final Set<TimedIdentifier> y) {
		final Long maxTimeX = maxTime(times(x));
		return Sets.filter(y, new Predicate<TimedIdentifier>() {
			@Override
			public boolean apply(TimedIdentifier i) {
				return !types(x)
						.contains(i.getIdentifier().getIdentifierType())
						|| maxTimeX == null || i.getTime() > maxTimeX;
			}

		});
	}

	private Set<Long> times(Set<TimedIdentifier> set) {
		return Functional.apply(set, new Function<TimedIdentifier, Long>() {
			@Override
			public Long apply(TimedIdentifier s) {
				return s.getTime();
			}
		});
	}

	private Long maxTime(Collection<Long> values) {
		if (values == null || values.size() == 0)
			return null;
		else
			return Collections.max(values);
	}

	private Set<TimedIdentifier> m(final Set<TimedIdentifier> x,
			final Set<TimedIdentifier> y) {
		if (strictSetComparator.compare(x, y) < 0)
			return Collections.EMPTY_SET;
		else {
			final Set<TimedIdentifier> g = g(x, y);
			final Set<IdentifierType> gTypes = types(g);
			Set<TimedIdentifier> a = Sets.filter(x,
					new Predicate<TimedIdentifier>() {
						@Override
						public boolean apply(final TimedIdentifier i) {
							return !gTypes.contains(i.getIdentifier()
									.getIdentifierType());
						}
					});
			return Sets.union(a, g);
		}
	}

	/**
	 * @param a
	 * @return
	 */
	public DatastoreImmutable add(final Set<TimedIdentifier> a) {
		Set<TimedIdentifier> pmza = pm(a);
		if (pmza.isEmpty())
			return new DatastoreImmutable(strictTypeComparator,
					strictSetComparator, Sets.union(z, ImmutableSet.of(a)));
		else {
			final Set<Set<TimedIdentifier>> intersecting = Sets.filter(z,
					new Predicate<Set<TimedIdentifier>>() {
						@Override
						public boolean apply(Set<TimedIdentifier> y) {
							return Sets.intersection(ids(y), ids(a)).size() > 0;
						}
					});
			Set<TimedIdentifier> fold = Functional.fold(intersecting,
					new Fold<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
						@Override
						public Set<TimedIdentifier> fold(
								Set<TimedIdentifier> previous,
								Set<TimedIdentifier> current) {

							Set<TimedIdentifier> result = m(previous, current);
							return result;
						}
					}, m(pmza, a));
			final Set<Identifier> foldIdentifiers = ids(fold);
			final Set<IdentifierType> foldTypes = types(fold);
			Set<Set<TimedIdentifier>> foldComplement = Functional.apply(z,
					new Function<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
						@Override
						public Set<TimedIdentifier> apply(
								final Set<TimedIdentifier> x) {
							final SetView<Identifier> intersectA = Sets
									.intersection(ids(x), ids(a));
							return Sets.filter(x,
									new Predicate<TimedIdentifier>() {
										@Override
										public boolean apply(TimedIdentifier i) {
											if (intersectA.size() == 0)
												return !foldIdentifiers
														.contains(i
																.getIdentifier());
											else
												return !foldTypes.contains(i
														.getIdentifier()
														.getIdentifierType());
										}
									});
						}
					});
			foldComplement = Sets.difference(foldComplement, ImmutableSet
					.of(ImmutableSet.of()));
			log.info("fold = " + fold + ", foldTypes = " + foldTypes
					+ " complement = " + foldComplement);

			SetView<Set<TimedIdentifier>> newZ = Sets.union(foldComplement,
					ImmutableSet.of(fold));
			return new DatastoreImmutable(strictTypeComparator,
					strictSetComparator, newZ);
		}
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("DataStoreImmutable=");
		for (Set<TimedIdentifier> set : z)
			s.append("\n" + set.toString());
		return s.toString();

	}
}

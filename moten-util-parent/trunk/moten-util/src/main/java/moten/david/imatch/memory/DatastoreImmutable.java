package moten.david.imatch.memory;

import static moten.david.imatch.memory.Util.ids;
import static moten.david.imatch.memory.Util.types;

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
import com.google.common.base.Predicates;
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
		return Sets.filter(y, new Predicate<TimedIdentifier>() {
			@Override
			public boolean apply(TimedIdentifier i) {
				TimedIdentifier j = getIdentifierOfType(x, i.getIdentifier()
						.getIdentifierType());
				return j == null || (i.getTime() > j.getTime());
			}
		});
	}

	private static TimedIdentifier getIdentifierOfType(Set<TimedIdentifier> x,
			IdentifierType identifierType) {
		for (TimedIdentifier i : x)
			if (i.getIdentifier().getIdentifierType().equals(identifierType))
				return i;
		// not found
		return null;
	}

	public Set<TimedIdentifier> product(final Set<TimedIdentifier> x,
			final Set<TimedIdentifier> y, final Set<TimedIdentifier> r) {
		if (y.size() == 0 || r.size() == 0)
			return x;
		else if (strictSetComparator.compare(r, y) < 0) {
			final Set<Identifier> yIds = ids(y);
			return Sets.filter(x, new Predicate<TimedIdentifier>() {
				@Override
				public boolean apply(TimedIdentifier i) {
					return !yIds.contains(i);
				}
			});
		} else {
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
		final Set<TimedIdentifier> pmza = pm(a);
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
			final Set<TimedIdentifier> fold = Functional.fold(intersecting,
					new Fold<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
						@Override
						public Set<TimedIdentifier> fold(
								Set<TimedIdentifier> previous,
								Set<TimedIdentifier> current) {
							Set<TimedIdentifier> result = product(previous,
									current, a);
							return result;
						}
					}, product(pmza, a, a));
			final Set<Identifier> foldIds = ids(fold);

			final Set<Set<TimedIdentifier>> zWithoutPmza = Sets.filter(z,
					Predicates.not(Predicates.equalTo(pmza)));
			Set<Set<TimedIdentifier>> foldComplement = Functional.apply(
					zWithoutPmza,
					new Function<Set<TimedIdentifier>, Set<TimedIdentifier>>() {
						@Override
						public Set<TimedIdentifier> apply(Set<TimedIdentifier> s) {
							return Sets.filter(s,
									new Predicate<TimedIdentifier>() {
										@Override
										public boolean apply(TimedIdentifier i) {
											return !foldIds.contains(i
													.getIdentifier());
										}
									});
						}
					});
			log.info("intersecting=" + intersecting + ", foldComplement="
					+ foldComplement + ", fold=" + fold);
			SetView<Set<TimedIdentifier>> newZ = Sets.union(foldComplement,
					ImmutableSet.of(fold));
			// remove empty sets
			newZ = Sets.difference(newZ, ImmutableSet.of(ImmutableSet.of()));
			return new DatastoreImmutable(strictTypeComparator,
					strictSetComparator, newZ);
		}
	}

	@Override
	public String toString() {
		if (z.size() == 0)
			return "empty";
		StringBuffer s = new StringBuffer();
		for (Set<TimedIdentifier> set : z) {
			if (s.length() > 0)
				s.append("\n");
			s.append(set.toString());
		}
		return s.toString();
	}
}

package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import moten.david.imatch.Datastore;
import moten.david.imatch.DatastoreBase;
import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;
import moten.david.imatch.IdentifierType;
import moten.david.imatch.IdentifierTypeSetFactory;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DatastoreImmutable extends DatastoreBase {

	private final ImmutableMap<Identifier, IdentifierSet> map;
	private final ImmutableMap<IdentifierSet, Double> times;
	private final IdentifierSetFactory identifierSetFactory;
	private final IdentifierTypeSetFactory identifierTypeSetFactory;
	private final IdentifierComparator identifierComparator;
	private final IdentifierTypeStrengthComparator identifierTypeStrengthComparator;

	@Inject
	public DatastoreImmutable(IdentifierSetFactory identifierSetFactory,
			IdentifierTypeSetFactory identifierTypeSetFactory,
			IdentifierComparator identifierComparator,
			IdentifierTypeStrengthComparator identifierTypeStrengthComparator,
			@Assisted Map<Identifier, IdentifierSet> map,
			@Assisted Map<IdentifierSet, Double> times) {
		super(identifierSetFactory, identifierTypeSetFactory);
		this.identifierSetFactory = identifierSetFactory;
		this.identifierTypeSetFactory = identifierTypeSetFactory;
		this.identifierComparator = identifierComparator;
		this.identifierTypeStrengthComparator = identifierTypeStrengthComparator;
		this.map = ImmutableMap.copyOf(map);
		this.times = ImmutableMap.copyOf(times);
	}

	@Override
	public IdentifierSet alpha(Identifier identifier) {
		IdentifierSet value = map.get(identifier);
		if (value == null)
			return MyIdentifierSet.EMPTY_SET;
		else
			return value;
	}

	@Override
	public double time(IdentifierSet set) {
		if (times.get(set) == null)
			return Double.MIN_NORMAL;
		else
			return times.get(set);
	}

	@Override
	public ImmutableSet<Identifier> identifiers() {
		return map.keySet();
	}

	@Override
	public Datastore add(IdentifierSet set, double time) {
		HashMap<Identifier, IdentifierSet> newMap = new HashMap<Identifier, IdentifierSet>();
		HashMap<IdentifierSet, Double> newTimes = new HashMap<IdentifierSet, Double>();
		for (Identifier i : Sets.union(map.keySet(), set.set())) {
			IdentifierSet s = merge(set, i);
			newMap.put(i, s);
			if (set.contains(i))
				newTimes.put(s, time);
			else
				newTimes.put(s, times.get(s));
		}
		return new DatastoreImmutable(identifierSetFactory,
				identifierTypeSetFactory, identifierComparator,
				identifierTypeStrengthComparator, newMap, newTimes);
	}

	@Override
	public double d(IdentifierType t) {
		return t.getOrder();
	}

	@Override
	public double dmax(IdentifierSet s) {
		if (s.set().size() == 0)
			return Double.MIN_VALUE;
		else {
			Identifier idmax = Collections.max(s.set(), identifierComparator);
			return idmax.getIdentifierType().getOrder();
		}
	}

	@Override
	public IdentifierSet nms(IdentifierSet x, final IdentifierSet y) {
		return x.filter(new Predicate<Identifier>() {
			@Override
			public boolean apply(Identifier i) {
				return !y.contains(i)
						&& i.getIdentifierType().getStrength() > Collections
								.max(y.types().set(),
										identifierTypeStrengthComparator)
								.getStrength();
			}
		});
	}

	@Override
	public IdentifierSet pm(IdentifierSet ids) {
		Set<IdentifierSet> alphaX = Functional.apply(ids.set(),
				new Function<Identifier, IdentifierSet>() {
					@Override
					public IdentifierSet apply(Identifier s) {
						return alpha(s);
					}
				});
		IdentifierSet y = Collections.max(alphaX,
				new Comparator<IdentifierSet>() {
					@Override
					public int compare(IdentifierSet o1, IdentifierSet o2) {
						return Double.compare(dmax(o1), dmax(o2));
					}
				});
		return y;
	}

	@Override
	public IdentifierType t(Identifier x) {
		return x.getIdentifierType();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("DataStoreImmutable=\n");
		for (Identifier i : map.keySet())
			s.append(i + " => " + map.get(i) + "\n");
		return s.toString();

	}

	@Override
	public IdentifierSet r(IdentifierSet set) {
		IdentifierSet r = identifierSetFactory.create();
		for (Identifier i : set.set())
			r = r.union(beta(set, i));
		return r;
	}

	@Override
	public IdentifierSet merge(IdentifierSet a, Identifier x) {
		if (a.contains(x))
			return r(a);
		else
			return alpha(x).complement(r(a));
	}

}

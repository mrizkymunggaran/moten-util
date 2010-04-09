package moten.david.imatch;

import java.util.Collections;
import java.util.Comparator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

public abstract class DatastoreBase implements Datastore {

	private final IdentifierSet emptySet;

	private final IdentifierSetFactory identitySetFactory;

	private final IdentifierTypeSetFactory typeSetFactory;

	@Inject
	public DatastoreBase(IdentifierSetFactory identifierSetFactory,
			IdentifierTypeSetFactory typeSetFactory) {
		this.identitySetFactory = identifierSetFactory;
		this.typeSetFactory = typeSetFactory;
		emptySet = identifierSetFactory.create();
	}

	@Override
	public IdentifierSet beta(IdentifierSet a, final Identifier x) {
		if (a.contains(x)) {
			if (a.isEmpty())
				return emptySet;
			else {
				final IdentifierSet pma = pm(a);
				if (pma.isEmpty())
					return a;
				else {
					IdentifierSet nms = nms(a, pma);
					if (nms.contains(x))
						return nms;
					else {
						IdentifierSet alphax = alpha(x);
						if (pma.equals(alphax))
							return pma;
						else if (strictOrdering().compare(t(x),
								max(alphax.types().set(), strictOrdering())) == 0) {
							IdentifierSet z = calculateZ(alphax, pma, a);
							final IdentifierTypeSet zTypes = getTypes(z);
							return pma.union(z).complement(
									pma.filter(new Predicate<Identifier>() {
										@Override
										public boolean apply(Identifier i) {
											return zTypes.contains(t(i));
										}
									}));
						} else if (time(a) > time(alphax)) {
							return pma.add(x).complement(
									pma.filter(new Predicate<Identifier>() {
										@Override
										public boolean apply(Identifier i) {
											return t(i).equals(t(x));
										}
									}));
						} else
							return pma;
					}
				}
			}
		} else {
			if (a.isEmpty())
				return emptySet;
			else {
				return alpha(x).complement(merge(a, a));
			}
		}
	}

	private IdentifierType max(ImmutableSet<IdentifierType> set,
			Comparator<IdentifierType> comparator) {
		if (set.size() == 0)
			return null;
		else
			return Collections.max(set, comparator);
	}

	private IdentifierTypeSet getTypes(final IdentifierSet ids) {
		IdentifierTypeSet set = typeSetFactory.create();
		for (Identifier i : ids.set())
			set = set.add(i.getIdentifierType());
		return set;
	}

	private IdentifierSet calculateZ(final IdentifierSet alphax,
			final IdentifierSet pma, final IdentifierSet a) {
		final IdentifierSet conflicts = alphax.conflicting(pma);
		return alphax.filter(new Predicate<Identifier>() {
			@Override
			public boolean apply(Identifier i) {
				return conflicts.contains(i) || time(alphax) < time(a);
			}
		});
	}

	private IdentifierSet merge(IdentifierSet a, IdentifierSet bSet) {
		IdentifierSet ids = identitySetFactory.create();
		for (Identifier b : bSet.set())
			ids = ids.union(merge(a, b));
		return ids;
	}

}

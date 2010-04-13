package moten.david.imatch;

import java.util.Collections;
import java.util.Comparator;

import com.google.inject.Inject;

public class IdentifierSetStrictComparator implements Comparator<IdentifierSet> {

	private final IdentifierTypeStrictComparator strictComparator;

	@Inject
	public IdentifierSetStrictComparator(
			IdentifierTypeStrictComparator strictComparator) {
		this.strictComparator = strictComparator;
	}

	@Override
	public int compare(IdentifierSet o1, IdentifierSet o2) {
		if (o1.isEmpty() && o2.isEmpty())
			return 0;
		else if (o1.isEmpty())
			return -1;
		else if (o2.isEmpty())
			return 1;
		else {
			IdentifierType t1 = Collections.max(o1.types().set(),
					strictComparator);
			IdentifierType t2 = Collections.max(o2.types().set(),
					strictComparator);
			return strictComparator.compare(t1, t2);
		}
	}

}

package moten.david.matchstack.memory;

import moten.david.matchstack.IdentifierType;
import moten.david.matchstack.IdentifierTypeStrengthComparator;
import moten.david.matchstack.IdentifierTypeStrictComparator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MyIdentifierTypeStrictComparator implements
		IdentifierTypeStrictComparator {

	private final IdentifierTypeStrengthComparator strengthComparator;

	@Inject
	public MyIdentifierTypeStrictComparator(
			IdentifierTypeStrengthComparator strengthComparator) {
		this.strengthComparator = strengthComparator;
	}

	@Override
	public int compare(IdentifierType o1, IdentifierType o2) {
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		else {
			int value = strengthComparator.compare(o1, o2);
			if (value == 0)
				return ((MyIdentifierType) o1).getName().compareTo(
						((MyIdentifierType) o2).getName());
			else
				return value;
		}
	}
}

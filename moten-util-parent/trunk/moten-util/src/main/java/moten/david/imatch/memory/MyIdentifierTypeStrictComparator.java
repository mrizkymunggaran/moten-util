package moten.david.imatch.memory;

import moten.david.imatch.IdentifierType;
import moten.david.imatch.IdentifierTypeStrengthComparator;
import moten.david.imatch.IdentifierTypeStrictComparator;

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
		int value = strengthComparator.compare(o1, o2);
		if (value == 0)
			return ((MyIdentifierType) o1).getName().compareTo(
					((MyIdentifierType) o2).getName());
		else
			return value;
	}
}

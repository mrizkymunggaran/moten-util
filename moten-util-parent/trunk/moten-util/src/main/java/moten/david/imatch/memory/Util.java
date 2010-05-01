package moten.david.imatch.memory;

import java.util.Set;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierType;
import moten.david.imatch.TimedIdentifier;
import moten.david.util.functional.Function;
import moten.david.util.functional.Functional;

public class Util {

	public static Set<Identifier> ids(Set<TimedIdentifier> set) {
		return Functional.apply(set,
				new Function<TimedIdentifier, Identifier>() {
					@Override
					public Identifier apply(TimedIdentifier s) {
						return s.getIdentifier();
					}
				});
	}

	public static Set<Set<Identifier>> idSets(Set<Set<TimedIdentifier>> sets) {
		return Functional.apply(sets,
				new Function<Set<TimedIdentifier>, Set<Identifier>>() {
					@Override
					public Set<Identifier> apply(Set<TimedIdentifier> s) {
						return ids(s);
					}
				});
	}

	public static Set<IdentifierType> types(Set<TimedIdentifier> set) {
		return Functional.apply(set,
				new Function<TimedIdentifier, IdentifierType>() {
					@Override
					public IdentifierType apply(TimedIdentifier s) {
						return s.getIdentifier().getIdentifierType();
					}
				});

	}

}

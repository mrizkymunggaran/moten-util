package moten.david.imatch;

import moten.david.util.functional.Function;

public class Constants {

	public static Function<TimedIdentifier, IdentifierType> identifierTypeExtractor = new Function<TimedIdentifier, IdentifierType>() {
		@Override
		public IdentifierType apply(TimedIdentifier s) {
			return s.getIdentifierType();
		}
	};
}

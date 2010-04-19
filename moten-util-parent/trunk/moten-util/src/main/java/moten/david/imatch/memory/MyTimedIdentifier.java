package moten.david.imatch.memory;

import moten.david.imatch.IdentifierType;
import moten.david.imatch.TimedIdentifier;

public class MyTimedIdentifier implements TimedIdentifier {

	private final long time;
	private final MyIdentifier identifier;

	public MyTimedIdentifier(MyIdentifier identifier, long time) {
		this.identifier = identifier;
		this.time = time;
	}

	public MyIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public IdentifierType getIdentifierType() {
		return identifier.getIdentifierType();
	}

}

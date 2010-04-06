package moten.david.imatch.memory;

import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

public class MyIdentifierSetFactory implements IdentifierSetFactory {

	@Override
	public IdentifierSet create() {
		return new MyIdentifierSet();
	}

}

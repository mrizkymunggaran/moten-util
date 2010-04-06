package moten.david.imatch.memory;

import moten.david.imatch.IdentifierTypeSet;
import moten.david.imatch.IdentifierTypeSetFactory;

public class MyIdentifierTypeSetFactory implements IdentifierTypeSetFactory {

	@Override
	public IdentifierTypeSet create() {
		return new MyIdentifierTypeSet();
	}

}

package moten.david.imatch.memory;

import moten.david.imatch.IdentifierTypeSet;
import moten.david.imatch.IdentifierTypeSetFactory;

import com.google.inject.Singleton;

@Singleton
public class MyIdentifierTypeSetFactory implements IdentifierTypeSetFactory {

    @Override
    public IdentifierTypeSet create() {
        return new MyIdentifierTypeSet();
    }

}

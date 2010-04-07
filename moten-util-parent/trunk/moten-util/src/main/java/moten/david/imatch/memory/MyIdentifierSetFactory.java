package moten.david.imatch.memory;

import moten.david.imatch.IdentifierSet;
import moten.david.imatch.IdentifierSetFactory;

import com.google.inject.Singleton;

@Singleton
public class MyIdentifierSetFactory implements IdentifierSetFactory {

    @Override
    public IdentifierSet create() {
        return new MyIdentifierSet();
    }

}

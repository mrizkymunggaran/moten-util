package guavax;

import java.util.Set;

class SetFacade<T> extends CollectionFacade<T> implements Set<T> {

    public SetFacade(Set<T> c) {
	super(c);
    }

}

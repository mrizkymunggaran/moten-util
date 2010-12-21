package guavax;

import java.util.Collection;

import com.google.common.base.Predicate;

public class FluentCollection<T> extends CollectionFacade<T> {

    private final Collection<T> c;

    public FluentCollection(Collection<T> c) {
	super(c);
	this.c = c;
    }

    public Option<T> find(Predicate<T> predicate) {
	for (T t : c)
	    if (predicate.apply(t))
		return new Some<T>(t);
	return new None<T>();
    }

}
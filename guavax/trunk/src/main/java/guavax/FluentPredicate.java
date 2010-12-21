package guavax;

import java.util.Collection;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class FluentPredicate<T> implements Predicate<T> {

    private final Predicate<T> predicate;

    public FluentPredicate(Predicate<T> predicate) {
	this.predicate = predicate;
    }

    public FluentPredicate<T> and(Predicate<T> p) {
	return create(Predicates.and(predicate, p));
    }

    public FluentPredicate<T> or(Predicate<T> p) {
	return create(Predicates.or(predicate, p));
    }

    public FluentPredicate<T> not(Predicate<T> p) {
	return create(Predicates.not(predicate));
    }

    public static <S> FluentPredicate<S> alwaysFalse() {
	Predicate<S> result = Predicates.alwaysFalse();
	return create(result);
    }

    public static <S> FluentPredicate<S> alwaysTrue() {
	Predicate<S> result = Predicates.alwaysTrue();
	return create(result);
    }

    public <A> FluentPredicate<A> compose(Predicate<T> p,
	    Function<A, ? extends T> f) {
	return create(Predicates.compose(predicate, f));
    }

    public static FluentPredicate<CharSequence> contains(Pattern pattern) {
	return create(Predicates.contains(pattern));
    }

    public static <S> FluentPredicate<S> equalTo(S t) {
	return create(Predicates.equalTo(t));
    }

    public static <S> FluentPredicate<S> in(Collection<S> coll) {
	return create(Predicates.in(coll));
    }

    private static <S> FluentPredicate<S> create(Predicate<S> p) {
	return new FluentPredicate<S>(p);
    }

    @Override
    public boolean apply(T input) {
	return predicate.apply(input);
    }

}

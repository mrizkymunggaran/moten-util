package guavax;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;

public class FluentSet<T> extends FluentCollection<T> {

	private final Set<T> set;

	public FluentSet(Set<T> set) {
		super(Preconditions.checkNotNull(set));
		this.set = set;
	}

	public FluentSet<T> filter(Predicate<T> predicate) {
		return create(Sets.filter(set, predicate));
	}

	public <S> FluentSet<S> map(Function<T, S> f) {
		Builder<S> builder = new Builder<S>();
		for (T t : set)
			builder.add(f.apply(t));
		return create(builder.build());
	}

	public FluentSet<T> difference(Set<T> s) {
		return create(Sets.difference(set, s));
	}

	public FluentSet<T> intersection(Set<T> s) {
		return create(Sets.intersection(set, s));
	}

	public FluentSet<T> union(Set<T> s) {
		return create(Sets.union(set, s));
	}

	public FluentSet<T> eval() {
		return create(Sets.newHashSet(set));
	}

	public void foreach(Function<T, Void> f) {
		for (T t : set)
			f.apply(t);
	}

	public FluentSet<T> symmetricDifference(Set<T> s) {
		return create(Sets.symmetricDifference(set, s));
	}

	private static <S> FluentSet<S> create(Set<S> s) {
		return new FluentSet<S>(s);
	}

	public <S> FluentSet<S> flatten(Function<T, Set<S>> f) {
		Builder<S> builder = new Builder<S>();
		for (T t : set)
			builder.addAll(f.apply(t));
		return create(builder.build());
	}

	public <R, S> FluentSet<S> flatMap(Function<T, R> f, Function<R, Set<S>> g) {
		return map(f).flatten(g);
	}

	public FluentSet<T> flatMap(Function<T, Set<T>> f, Class<T> cls) {
		return flatMap(f, identity(cls));
	}

	public static <S> Function<Set<S>, Set<S>> identity(Class<S> cls) {
		return new Function<Set<S>, Set<S>>() {
			@Override
			public Set<S> apply(Set<S> input) {
				return input;
			}
		};
	}

	public FluentSet<T> plus(T t) {
		return union(ImmutableSet.of(t));
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return set.equals(o);
	}

	@Override
	public String toString() {
		return set.toString();
	}
}
package moten.david.util.functional;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Functional {

	public static <S, T> Set<T> apply(Set<S> set, Function<S, T> f) {
		Set<T> result = new HashSet<T>();
		for (S s : set)
			result.add(f.apply(s));
		return ImmutableSet.copyOf(result);
	}

	public static <T, S> S fold(Set<T> set, Fold<T, S> fold, S initialValue) {
		S value = initialValue;
		for (T t : set)
			value = fold.fold(value, t);
		return value;
	}
}

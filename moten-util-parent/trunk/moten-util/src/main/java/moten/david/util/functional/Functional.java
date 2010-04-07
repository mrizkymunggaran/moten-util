package moten.david.util.functional;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class Functional {

    public static <S, T> Set<T> apply(Set<S> set, Function<S, T> f) {
        Builder<T> builder = ImmutableSet.builder();
        for (S s : set)
            builder.add(f.apply(s));
        return builder.build();
    }

    public static <T> T fold(Set<T> set, Fold<T> fold, T initialValue) {
        T value = initialValue;
        for (T t : set)
            value = fold.fold(value, t);
        return value;
    }
}

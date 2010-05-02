package moten.david.util.functional;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class Functional {

	public static <S, T> Set<T> apply(Set<S> set, Function<S, T> f) {
		Builder<T> builder = ImmutableSet.builder();
		for (S s : set)
			builder.add(f.apply(s));
		return builder.build();
	}

	public static <T, S> S fold(Set<T> set, Fold<T, S> fold, S initialValue) {
		S value = initialValue;
		for (T t : set)
			value = fold.fold(value, t);
		return value;
	}

	public static <T> Set<T> filter(Set<T> set, Predicate<T> predicate) {
		Builder<T> builder = ImmutableSet.builder();
		for (T s : set)
			if (predicate.apply(s))
				builder.add(s);
		return builder.build();
	}

	public static <S, T> Set<T> apply(Set<S> set, final Function<S, T> f,
			final ExecutorService executorService) {
		com.google.common.collect.ImmutableList.Builder<Future<T>> futures = ImmutableList
				.builder();
		for (final S s : set)
			futures.add(executorService.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					return f.apply(s);
				}
			}));

		Builder<T> builder = ImmutableSet.builder();
		for (Future<T> future : futures.build()) {
			T result;
			try {
				result = future.get();
				builder.add(result);
			} catch (InterruptedException e) {
				// do nothing
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		return builder.build();
	}

	public static <T> Set<T> filter(Set<T> set, final Predicate<T> predicate,
			ExecutorService executorService) {
		com.google.common.collect.ImmutableList.Builder<Future<T>> futures = ImmutableList
				.builder();
		for (final T s : set)
			futures.add(executorService.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					if (predicate.apply(s))
						return s;
					else
						return null;
				}
			}));

		Builder<T> builder = ImmutableSet.builder();
		for (Future<T> future : futures.build()) {
			T result;
			try {
				result = future.get();
				if (result != null)
					builder.add(result);
			} catch (InterruptedException e) {
				// do nothing
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		return builder.build();
	}
}

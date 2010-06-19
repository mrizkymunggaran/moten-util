package moten.david.util.functional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableSet.Builder;

public class Functional {

    public static <S, T> Set<T> apply(Set<S> set, Function<S, T> f) {
        return apply(set.iterator(), f);
    }

    public static <S, T> Set<T> apply(Iterator<S> iterator, Function<S, T> f) {
        Builder<T> builder = ImmutableSet.builder();
        while (iterator.hasNext())
            builder.add(f.apply(iterator.next()));
        return builder.build();
    }

    public static <S, T> Set<T> apply(Set<S> set, final Function<S, T> f,
            final ExecutorService executorService, final int partitionSize) {
        return apply(set.iterator(), f, executorService, partitionSize);
    }

    public static <T, S> S fold(Set<T> set, Fold<T, S> fold, S initialValue) {
        S value = initialValue;
        for (T t : set)
            value = fold.fold(value, t);
        return value;
    }

    public static <T> Set<T> filter(Iterator<T> iterator, Predicate<T> predicate) {
        Builder<T> builder = ImmutableSet.builder();
        while (iterator.hasNext()) {
            T s = iterator.next();
            if (predicate.apply(s))
                builder.add(s);
        }
        return builder.build();
    }

    public static <T> Set<T> filter(Set<T> set, Predicate<T> predicate) {
        return filter(set.iterator(), predicate);
    }

    public static <S, T> Set<T> apply(Iterator<S> iterator,
            final Function<S, T> f, final ExecutorService executorService,
            final int partitionSize) {
        com.google.common.collect.ImmutableList.Builder<Future<Set<T>>> futures = ImmutableList
                .builder();
        final UnmodifiableIterator<List<S>> partitions = Iterators.partition(
                iterator, partitionSize);
        while (partitions.hasNext()) {
            final List<S> partition = partitions.next();
            futures.add(executorService.submit(new Callable<Set<T>>() {
                @Override
                public Set<T> call() throws Exception {
                    return apply(partition.iterator(), f);
                }
            }));
        }

        Builder<T> builder = ImmutableSet.builder();
        for (Future<Set<T>> future : futures.build()) {
            try {
                Set<T> result = future.get();
                builder.addAll(result);
            } catch (InterruptedException e) {
                // do nothing
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }

    public static <T> Set<T> filter(Set<T> set, final Predicate<T> predicate,
            ExecutorService executorService, int partitionSize) {
        return filter(set.iterator(), predicate, executorService, partitionSize);
    }

    public static <T> Set<T> filter(Iterator<T> iterator,
            final Predicate<T> predicate, ExecutorService executorService,
            int partitionSize) {
        com.google.common.collect.ImmutableList.Builder<Future<Set<T>>> futures = ImmutableList
                .builder();
        UnmodifiableIterator<List<T>> partitions = Iterators.partition(
                iterator, partitionSize);

        while (partitions.hasNext()) {
            final List<T> partition = partitions.next();
            futures.add(executorService.submit(new Callable<Set<T>>() {
                @Override
                public Set<T> call() throws Exception {
                    return filter(partition.iterator(), predicate);
                }
            }));
        }
        Builder<T> builder = ImmutableSet.builder();
        for (Future<Set<T>> future : futures.build()) {
            try {
                Set<T> result = future.get();
                if (result != null)
                    builder.addAll(result);
            } catch (InterruptedException e) {
                // do nothing
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }

}

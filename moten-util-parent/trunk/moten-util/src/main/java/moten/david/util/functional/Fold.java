package moten.david.util.functional;

public interface Fold<T, S> {
	S fold(S lastValue, T t);
}

package moten.david.imatch.memory;

import java.util.Collections;
import java.util.Set;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;

public class MyIdentifierSet implements IdentifierSet {

	private final ImmutableSet<Identifier> set;

	private MyIdentifierSet(Set<Identifier> set) {
		Builder<Identifier> builder = ImmutableSet.builder();
		builder.add(set.toArray(new Identifier[] {}));
		this.set = builder.build();
	}

	public MyIdentifierSet() {
		this(Collections.EMPTY_SET);
	}

	@Override
	public IdentifierSet add(Identifier identifier) {
		Builder<Identifier> builder = ImmutableSet.builder();
		builder.add(set.toArray(new Identifier[] {}));
		builder.add(identifier);
		return new MyIdentifierSet(builder.build());
	}

	@Override
	public IdentifierSet complement(IdentifierSet identifierSet) {
		return new MyIdentifierSet(Sets.difference(set, identifierSet.set()));
	}

	@Override
	public boolean contains(Identifier identifier) {
		return set.contains(identifier);
	}

	@Override
	public boolean equals(Object o) {
		return Objects.equal(set, ((MyIdentifierSet) o).set);
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}

	@Override
	public IdentifierSet filter(Predicate<Identifier> predicate) {
		Builder<Identifier> builder = ImmutableSet.builder();
		for (Identifier id : set)
			if (predicate.apply(id))
				builder.add(id);
		return new MyIdentifierSet(builder.build());
	}

	@Override
	public boolean isEmpty() {
		return set.size() == 0;
	}

	@Override
	public ImmutableSet<Identifier> set() {
		return set;
	}

	@Override
	public IdentifierSet union(IdentifierSet s) {
		return new MyIdentifierSet(Sets.union(set, s.set()));
	}
}
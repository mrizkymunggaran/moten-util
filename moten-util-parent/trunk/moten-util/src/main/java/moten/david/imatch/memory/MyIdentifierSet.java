package moten.david.imatch.memory;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.imatch.Identifier;
import moten.david.imatch.IdentifierSet;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class MyIdentifierSet implements IdentifierSet {

	private final SortedSet<Identifier> set;

	private MyIdentifierSet(Set<Identifier> set) {
		this.set = new TreeSet<Identifier>(set);
	}

	public MyIdentifierSet() {
		this.set = new TreeSet<Identifier>();
	}

	@Override
	public IdentifierSet add(Identifier identifier) {
		TreeSet<Identifier> s = new TreeSet<Identifier>(set);
		s.add(identifier);
		return new MyIdentifierSet(s);
	}

	@Override
	public IdentifierSet complement(IdentifierSet identifierSet) {
		return new MyIdentifierSet(Sets.difference(set,
				((MyIdentifierSet) identifierSet).set));
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
	public IdentifierSet filter(Predicate predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		return set.size() == 0;
	}

	@Override
	public ImmutableList<Identifier> list() {
		// TODO
		return null;
	}

	@Override
	public IdentifierSet union(IdentifierSet set) {
		// TODO Auto-generated method stub
		return null;
	}

}

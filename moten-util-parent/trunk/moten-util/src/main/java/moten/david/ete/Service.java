package moten.david.ete;

import java.util.Set;

/**
 * An algorithm for adding a fix to the entity tracking engine.
 * 
 * @author dxm
 */
public interface Service {
	void addFix(Fix fix);

	void remove(Set<? extends Identifier> identifiers);

	void merge(Set<? extends Identifier> identifiers);
}

package moten.david.matchstack.memory;

import java.util.Set;

import moten.david.matchstack.TimedIdentifier;

public interface IntersectionProvider {
	Set<TimedIdentifier> getPrimaryMatch(Set<TimedIdentifier> a);

	Set<Set<TimedIdentifier>> getIntersection(Set<TimedIdentifier> a);

	Set<Set<TimedIdentifier>> getNonIntersection(Set<TimedIdentifier> a);
}

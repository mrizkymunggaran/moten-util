package moten.david.ete;

import java.util.Set;

public interface Entity {
	Set<Identifier> getIdentifiers();

	void addFix(Fix fix);

	boolean isPrimaryIdentifier(Identifier identifier);

	void addIdentifier(Identifier id);

	void removeIdentifier(Identifier identifier);

	boolean weaker(Entity identifierEntity);

	Object getIdentifier(IdentifierType identifierType);

	void setIdentifier(IdentifierType identifierType, Identifier identifier);
}

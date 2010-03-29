package moten.david.ete;

import java.util.Collections;

public class NewFixAlgorithmImpl implements NewFixAlgorithm {

	private final Engine engine;

	public NewFixAlgorithmImpl(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void addFix(Fix fix) {
		if (engine.hasFixAlready(fix))
			return;
		Entity primaryEntity = engine.findEntity(fix.getIdentifiers());
		if (primaryEntity == null)
			primaryEntity = engine.createEntity(fix.getIdentifiers());
		primaryEntity.addFix(fix);
		for (Identifier identifier : fix.getIdentifiers()) {
			// if the identifier is not the primary identifier on the primary
			// entity
			if (!primaryEntity.isPrimaryIdentifier(identifier)) {
				// get the entity corresponding to the identity
				Entity identityEntity = engine.findEntity(Collections
						.singleton(identifier));
				// if the identifier is on another entity
				if (!primaryEntity.equals(identityEntity)) {

				}

			}
		}
	}

}
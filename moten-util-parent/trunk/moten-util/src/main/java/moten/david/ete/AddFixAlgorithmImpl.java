package moten.david.ete;

import java.util.Collection;
import java.util.Collections;

public class AddFixAlgorithmImpl implements AddFixAlgorithm {

	private final Engine engine;

	public AddFixAlgorithmImpl(Engine engine) {
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
			if (!getPrimaryIdentifier(primaryEntity).equals(identifier)) {
				// get the entity corresponding to the identity
				Entity identifierEntity = engine.findEntity(Collections
						.singleton(identifier));
				// if the identifier is on another entity
				if (!primaryEntity.equals(identifierEntity)) {
					// if the identifier is the primary identifier on the other
					// entity
					if (getPrimaryIdentifier(identifierEntity).equals(
							identifier)) {
						// if merge condition satisfied
						if (mergeOk(primaryEntity, identifierEntity, fix)) {
							// merge
							for (Identifier id : identifierEntity
									.getIdentifiers()) {
								// if id does not conflict with an identifier on
								// the primary entity
								if (!conflicts(id, primaryEntity
										.getIdentifiers())) {
									// move the identifier
									primaryEntity.getIdentifiers().add(id);
								}
							}
							identifierEntity.moveFixes(primaryEntity);
						} else {
							// remove identifiers matching the primary entity
							// from the current fix
							for (Identifier id : primaryEntity.getIdentifiers()) {
								fix.getIdentifiers().remove(id);
							}
							identifierEntity.addFix(fix);
						}
					} else {
						// if the primary entity is stronger than or of the same
						// strength as the other entity then
						// move the identifier to the primary entity
						// end if
						if (!weaker(primaryEntity, identifierEntity)) {
							identifierEntity.getIdentifiers()
									.remove(identifier);
							primaryEntity.getIdentifiers().add(identifier);
						}
					}
					// if the identifier type is on the primary entity
					// update primary entity with fix identifier value
					// end if
					if (getIdentifier(primaryEntity, identifier
							.getIdentifierType()) != null)
						setIdentifier(primaryEntity, identifier);
				} else // if the identifier type is on the primary entity
				// update primary entity with fix identifier value
				// end if
				if (getIdentifier(primaryEntity, identifier.getIdentifierType()) != null)
					setIdentifier(primaryEntity, identifier);
				else {
					// add the new entity identity from the fix identifier to
					// the primary entity
					primaryEntity.getIdentifiers().add(identifier);
				}
			}
		}
	}

	private void setIdentifier(Entity entity, Identifier identifier) {
		Identifier identifierToReplace = null;
		for (Identifier id : entity.getIdentifiers())
			if (id.getIdentifierType().equals(identifier.getIdentifierType()))
				identifierToReplace = id;
		if (identifierToReplace != null)
			entity.getIdentifiers().remove(identifierToReplace);
		entity.getIdentifiers().add(identifier);
	}

	private Object getIdentifier(Entity entity, IdentifierType identifierType) {
		return getIdentifier(entity.getIdentifiers(), identifierType);
	}

	private Object getIdentifier(Collection<Identifier> identifiers,
			IdentifierType identifierType) {
		for (Identifier id : identifiers)
			if (id.getIdentifierType().equals(identifierType))
				return id;
		return null;
	}

	private Identifier getPrimaryIdentifier(Entity entity) {
		return entity.getIdentifiers().last();
	}

	private boolean weaker(Entity primaryEntity, Entity identifierEntity) {
		return getPrimaryIdentifier(primaryEntity).getIdentifierType()
				.compareTo(
						getPrimaryIdentifier(identifierEntity)
								.getIdentifierType()) < 0;
	}

	/**
	 * <ol>
	 * <li>The nominated maximum speed for the fix entity type does not exist</li>
	 * </ol>
	 * <p>
	 * OR
	 * </p>
	 * <ol>
	 * <li>the calculated average speed to move from the latest existing primary
	 * entity fix to the current fix position is less than a nominated maximum
	 * (where specified) for the entity type (e.g. 50 knots for a vessel).</li>
	 * <li>the time difference between the two fixes is &gt;= N seconds (for all
	 * entity types). N should be configurable. N=60s seems reasonable</li>
	 * </ol>
	 * .
	 * 
	 * @param primaryEntity
	 * @param otherEntity
	 * @param fix
	 * @return
	 */
	private boolean mergeOk(Entity primaryEntity, Entity otherEntity, Fix fix) {
		if (fix.getType().equals(primaryEntity.getType())
				&& fix.getType().equals(otherEntity.getType()))
			return true;
		if (fix.getType().getMaximumSpeedMetresPerSecond() == null)
			return true;
		else {
			Fix latestPrimaryFix = primaryEntity.getLatestFix();
			double timeSeconds = (latestPrimaryFix.getTime().getTimeInMillis() - fix
					.getTime().getTimeInMillis()) / 1000.0;
			if (timeSeconds <= fix.getType()
					.getMinimumTimeForSpeedCalculationSeconds().doubleValue())
				return false;
			double distanceMetres = createPosition(latestPrimaryFix)
					.getDistanceToKm(createPosition(fix)) * 1000;
			if ((distanceMetres / timeSeconds) <= fix.getType()
					.getMaximumSpeedMetresPerSecond().doubleValue())
				return true;
			else
				return false;
		}
	}

	private moten.david.util.navigation.Position createPosition(Fix fix) {
		moten.david.util.navigation.Position p = new moten.david.util.navigation.Position(
				fix.getPosition().getLatitude().doubleValue(), fix
						.getPosition().getLongitude().doubleValue());
		return p;
	}

	private boolean conflicts(Identifier id, Collection<Identifier> identifiers) {
		for (Identifier identifier : identifiers)
			if (id.getIdentifierType().equals(identifier.getIdentifierType())
					&& !id.equals(identifier))
				return true;
		return false;
	}

}
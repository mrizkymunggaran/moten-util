package moten.david.ete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.inject.Inject;

/**
 * Implementation of add fix algorithm.
 * 
 * @author dave
 */
public class ServiceImpl implements Service {

	private final Engine engine;

	/**
	 * Constructs an instance using the given Engine parameter.
	 * 
	 * @param engine
	 */
	@Inject
	public ServiceImpl(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void addFix(Fix fix) {
		// TODO update wiki with this change
		// find the entity based on the identifiers in descending order
		Entity primaryEntity = engine.findEntity(fix.getIdentifiers());
		if (primaryEntity != null) {
			// stop if we already have this fix
			if (primaryEntity.hasFixAlready(fix))
				return;
			// TODO update wiki
			// if matching identity does not match the strongest identity on the
			// fix then create a new fix with the stronger identities.
			SortedSet<Identifier> set = getStrongerNonMatchingIdentifiers(
					primaryEntity.getIdentifiers().set(), fix.getIdentifiers());
			if (set.size() > 0) {
				primaryEntity = engine.createEntity(set);
			}
		} else
			// create the entity as no match was found
			primaryEntity = engine.createEntity(fix.getIdentifiers());

		// associate the fix with the primary entity
		primaryEntity.addFix(fix);
		Set<Identifier> identifiersRemovedFromFix = new HashSet<Identifier>();
		// process all of the identifiers on the fix, use a copy so that we can
		// remove fix identifiers as we go if we wish
		for (Identifier identifier : new TreeSet<Identifier>(fix
				.getIdentifiers())) {
			// if the identifier is not the primary identifier on the primary
			// entity
			if (!identifiersRemovedFromFix.contains(identifier)
					&& !isPrimaryIdentifier(primaryEntity, identifier)) {
				// get the entity corresponding to the identity
				Entity identifierEntity = engine
						.findEntity(new TreeSet<Identifier>(Collections
								.singleton(identifier)));
				// TODO update wiki
				// if the identifier was found and is on another entity
				if (identifierEntity != null
						&& !primaryEntity.equals(identifierEntity)) {
					// if the identifier is the primary identifier on the other
					// entity
					if (isPrimaryIdentifier(identifierEntity, identifier)) {
						processPrimaryIdentifierOnDifferentEntity(
								primaryEntity, identifierEntity, fix,
								identifiersRemovedFromFix);
					} else {
						processSecondaryIdentifierOnDifferentEntity(
								primaryEntity, identifierEntity, identifier,
								fix);
					}
					// if the identifier type is on the primary entity
					// update primary entity with fix identifier value
					// end if

					// we are going to place the fix identifier against the
					// primary entity. If it exists against another entity then
					// remove it from that entity
					if (identifierEntity.getIdentifiers().set().contains(
							identifier))
						identifierEntity.getIdentifiers().remove(identifier);

					// if the other entity has no more identifiers left after
					// removal of fix identifier then remove the entity
					if (identifierEntity.getIdentifiers().set().size() == 0) {
						identifierEntity.moveFixesTo(primaryEntity);
						engine.removeEntity(identifierEntity);
					}
					// place the identifier against the primary entity
					if (getIdentifier(primaryEntity, identifier
							.getIdentifierType()) != null)
						// if identifier type exists then replace it
						setIdentifier(primaryEntity, identifier);
					else
						// otherwise add it
						primaryEntity.getIdentifiers().add(identifier);
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

	private void processSecondaryIdentifierOnDifferentEntity(
			Entity primaryEntity, Entity identifierEntity,
			Identifier identifier, Fix fix) {
		// TODO update wiki with this change
		// if the primary entity is stronger than or of the same
		// strength as the other entity then
		// move the identifier to the primary entity
		// end if
		if (stronger(primaryEntity, identifierEntity)
				|| (sameStrength(primaryEntity, identifierEntity) && fix
						.getTime().after(
								identifierEntity.getLatestFix().getTime()))) {
			identifierEntity.getIdentifiers().remove(identifier);
			setIdentifier(primaryEntity, identifier);
		}

	}

	private void processPrimaryIdentifierOnDifferentEntity(
			Entity primaryEntity, Entity identifierEntity, Fix fix,
			Set<Identifier> identifiersRemovedFromFix) {
		// if merge condition satisfied
		if (mergeConditionSatisfied(primaryEntity, identifierEntity, fix)) {
			// merge
			for (Identifier id : identifierEntity.getIdentifiers().set()) {
				// if id does not conflict with an identifier on
				// the primary entity
				if (!conflicts(id, primaryEntity.getIdentifiers().set())) {
					// move the identifier
					primaryEntity.getIdentifiers().add(id);
				}
			}
			identifierEntity.moveFixesTo(primaryEntity);
			engine.removeEntity(identifierEntity);
		} else {
			// merge rejected so remove conflicting identifiers from the other
			// entity from the fix
			for (Identifier id : identifierEntity.getIdentifiers().set()) {
				if (fix.getIdentifiers().remove(id))
					identifiersRemovedFromFix.add(id);
			}
		}
	}

	/**
	 * Finds all identifiers that are stronger than the <i>a</i> identifiers and
	 * not in the <i>a</i> set.
	 * 
	 * @param a
	 * @param fixIds
	 * @return
	 */
	private SortedSet<Identifier> getStrongerNonMatchingIdentifiers(
			SortedSet<? extends Identifier> a,
			SortedSet<? extends Identifier> fixIds) {
		TreeSet<Identifier> tree = new TreeSet<Identifier>();
		Identifier matchingIdentifier = null;
		for (Identifier id : fixIds)
			if (matchingIdentifier == null && a.contains(id)) {
				matchingIdentifier = id;
			}
		if (matchingIdentifier != null)
			for (Identifier id : fixIds)
				if (!a.contains(id)
						&& id.getIdentifierType().getStrength() > matchingIdentifier
								.getIdentifierType().getStrength())
					tree.add(id);
		return tree;
	}

	/**
	 * If the entity already has an Identifier of the same type as
	 * <i>identifier</i> then replace that identifier otherwise add
	 * <i>identiifer</i> to the entity identifiers.
	 * 
	 * @param entity
	 * @param identifier
	 */
	private void setIdentifier(Entity entity, Identifier identifier) {
		Identifier identifierToReplace = null;
		for (Identifier id : entity.getIdentifiers().set())
			if (id.getIdentifierType().equals(identifier.getIdentifierType()))
				identifierToReplace = id;
		if (identifierToReplace != null)
			entity.getIdentifiers().remove(identifierToReplace);
		entity.getIdentifiers().add(identifier);
	}

	/**
	 * Get the identifier of the entity that has the given type. If no
	 * identifier of that type is found then returns null.
	 * 
	 * @param entity
	 * @param identifierType
	 * @return
	 */
	private Object getIdentifier(Entity entity, IdentifierType identifierType) {
		return getIdentifier(entity.getIdentifiers().set(), identifierType);
	}

	/**
	 * Get the identifier from the collection that has the given type. If no
	 * identifier of that type is found then returns null.
	 * 
	 * @param identifiers
	 * @param identifierType
	 * @return
	 */
	private Identifier getIdentifier(
			Collection<? extends Identifier> identifiers,
			IdentifierType identifierType) {
		for (Identifier id : identifiers)
			if (id.getIdentifierType().equals(identifierType))
				return id;
		return null;
	}

	/**
	 * Get the primary identifier of the entity.
	 * 
	 * @param entity
	 * @return
	 */
	private Identifier getPrimaryIdentifier(Entity entity) {
		return entity.getIdentifiers().set().first();
	}

	/**
	 * Returns true if and only if the primary identifier of the entity is
	 * <i>identifier</i>.
	 * 
	 * @param entity
	 * @param identifier
	 * @return
	 */
	private boolean isPrimaryIdentifier(Entity entity, Identifier identifier) {
		return getPrimaryIdentifier(entity).equals(identifier);
	}

	/**
	 * Returns true if and only if the type of the primary identifier of entity
	 * <i>a</i> is stronger than (ordered before) the type of the primary
	 * identifier of entity <i>b</i>.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean stronger(Entity a, Entity b) {
		return getPrimaryIdentifier(a).getIdentifierType().getStrength() > getPrimaryIdentifier(
				b).getIdentifierType().getStrength();
	}

	/**
	 * Returns true if and only if the type of the primary identifier of entity
	 * <i>a</i> is the same strength as the type of the primary identifier of
	 * entity <i>b</i>.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean sameStrength(Entity a, Entity b) {
		return getPrimaryIdentifier(a).getIdentifierType().getStrength() == getPrimaryIdentifier(
				b).getIdentifierType().getStrength();
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
	private boolean mergeConditionSatisfied(Entity primaryEntity,
			Entity otherEntity, Fix fix) {

		if (primaryEntity.getMaximumSpeedMetresPerSecond() == null)
			return true;
		else {
			Fix latestPrimaryFix = primaryEntity.getLatestFix();
			double timeSeconds = (latestPrimaryFix.getTime().getTimeInMillis() - fix
					.getTime().getTimeInMillis()) / 1000.0;
			if (timeSeconds <= primaryEntity
					.getMinimumTimeForSpeedCalculationSeconds().doubleValue())
				return false;
			double distanceMetres = createPosition(latestPrimaryFix)
					.getDistanceToKm(createPosition(fix)) * 1000;
			if ((distanceMetres / timeSeconds) <= primaryEntity
					.getMaximumSpeedMetresPerSecond().doubleValue())
				return true;
			else
				return false;
		}
	}

	/**
	 * Create a moten.david.util.navigation.Position from a Fix.
	 * 
	 * @param fix
	 * @return
	 */
	private moten.david.util.navigation.Position createPosition(Fix fix) {
		moten.david.util.navigation.Position p = new moten.david.util.navigation.Position(
				fix.getPosition().getLatitude().doubleValue(), fix
						.getPosition().getLongitude().doubleValue());
		return p;
	}

	/**
	 * Returns true if and only if the collection has an identifier of the same
	 * type as id but an equals comparison returns false.
	 * 
	 * @param id
	 * @param identifiers
	 * @return
	 */
	private boolean conflicts(Identifier id,
			Collection<? extends Identifier> identifiers) {
		for (Identifier identifier : identifiers)
			if (id.getIdentifierType().equals(identifier.getIdentifierType())
					&& !id.equals(identifier))
				return true;
		return false;
	}

	@Override
	public void merge(Set<? extends Identifier> identifiers) {
		Set<Entity> entities = new HashSet<Entity>();
		for (final Identifier id : identifiers) {
			Entity entity = engine.findEntity(new TreeSet<Identifier>() {
				{
					add(id);
				}
			});
			if (entity != null)
				entities.add(entity);
		}
		mergeEntities(entities);
	}

	/**
	 * Merges a set of entities into the strongest entity.
	 * 
	 * @param entities
	 */
	private void mergeEntities(Set<Entity> entities) {
		Entity strongest = getStrongest(entities);
		for (Entity entity : entities) {
			if (entity != strongest) {
				for (Identifier id : entity.getIdentifiers().set()) {
					entity.getIdentifiers().remove(id);
					strongest.getIdentifiers().add(id);
				}
				entity.moveFixesTo(strongest);
			}
		}
	}

	/**
	 * Returns the strongest entity in terms of identifier type from a set of
	 * entities.
	 * 
	 * @param entities
	 * @return
	 */
	private Entity getStrongest(Set<Entity> entities) {
		Entity strongest = null;
		for (Entity entity : entities) {
			if (strongest == null)
				strongest = entity;
			else if (getPrimaryIdentifier(strongest).getIdentifierType()
					.getStrength() < getPrimaryIdentifier(entity)
					.getIdentifierType().getStrength())
				strongest = entity;
		}
		return strongest;
	}

	@Override
	public void remove(Set<? extends Identifier> identifiers) {
		Entity entity = engine.findEntity(new TreeSet<Identifier>(identifiers));
		if (entity != null)
			for (Identifier id : identifiers)
				entity.getIdentifiers().remove(id);

	}
}
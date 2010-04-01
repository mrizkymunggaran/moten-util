package moten.david.ete;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import com.google.inject.Inject;

/**
 * Implementation of add fix algorithm.
 * 
 * @author dave
 */
public class FixAdderImpl implements FixAdder {

    private final Engine engine;

    /**
     * Constructs an instance using the given Engine parameter.
     * 
     * @param engine
     */
    @Inject
    public FixAdderImpl(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void addFix(Fix fix) {

        // find the entity based on the identifiers in descending order
        Entity primaryEntity = engine.findEntity(fix.getIdentifiers());
        if (primaryEntity != null) {
            // stop if we already have this fix
            if (primaryEntity.hasFixAlready(fix))
                return;
        } else
            // create the entity as no match was found
            primaryEntity = engine.createEntity(fix.getIdentifiers());

        // associate the fix with the primary entity
        primaryEntity.addFix(fix);
        // process all of the identifiers on the fix
        for (Identifier identifier : fix.getIdentifiers()) {
            // if the identifier is not the primary identifier on the primary
            // entity
            if (!isPrimaryIdentifier(primaryEntity, identifier)) {
                // get the entity corresponding to the identity
                Entity identifierEntity = engine
                        .findEntity(new TreeSet<Identifier>(Collections
                                .singleton(identifier)));
                // if the identifier is on another entity
                if (!primaryEntity.equals(identifierEntity)) {
                    // if the identifier is the primary identifier on the other
                    // entity
                    if (isPrimaryIdentifier(identifierEntity, identifier)) {
                        // if merge condition satisfied
                        if (mergeConditionSatisfied(primaryEntity,
                                identifierEntity, fix)) {
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
        for (Identifier id : entity.getIdentifiers())
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
        return getIdentifier(entity.getIdentifiers(), identifierType);
    }

    /**
     * Get the identifier from the collection that has the given type. If no
     * identifier of that type is found then returns null.
     * 
     * @param identifiers
     * @param identifierType
     * @return
     */
    private Object getIdentifier(Collection<Identifier> identifiers,
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
        return entity.getIdentifiers().last();
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
     * <i>a</i> is weaker than (ordered before) the type of the primary
     * identifier of entity <i>b</i>.
     * 
     * @param a
     * @param b
     * @return
     */
    private boolean weaker(Entity a, Entity b) {
        return getPrimaryIdentifier(a).getIdentifierType().compareTo(
                getPrimaryIdentifier(b).getIdentifierType()) < 0;
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
    private boolean conflicts(Identifier id, Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers)
            if (id.getIdentifierType().equals(identifier.getIdentifierType())
                    && !id.equals(identifier))
                return true;
        return false;
    }

}
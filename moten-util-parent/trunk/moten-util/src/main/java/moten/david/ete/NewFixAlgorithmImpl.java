package moten.david.ete;

import java.util.Collections;
import java.util.Set;

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
                Entity identifierEntity = engine.findEntity(Collections
                        .singleton(identifier));
                // if the identifier is on another entity
                if (!primaryEntity.equals(identifierEntity)) {
                    // if the identifier is the primary identifier on the other
                    // entity
                    if (identifierEntity.isPrimaryIdentifier(identifier)) {
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
                                    primaryEntity.addIdentifier(id);
                                }
                            }
                        } else {
                            // remove identifiers matching the primary entity
                            // from the current fix
                            for (Identifier id : primaryEntity.getIdentifiers()) {
                                fix.removeIdentifier(id);
                            }
                            identifierEntity.addFix(fix);
                        }
                    } else {
                        // if the primary entity is stronger than or of the same
                        // strength as the other entity then
                        // move the identifier to the primary entity
                        // end if
                        if (!primaryEntity.weaker(identifierEntity)) {
                            identifierEntity.removeIdentifier(identifier);
                            primaryEntity.addIdentifier(identifier);
                        }
                    }
                    // if the identifier type is on the primary entity
                    // update primary entity with fix identifier value
                    // end if
                    if (primaryEntity.getIdentifier(identifier
                            .getIdentifierType()) != null)
                        primaryEntity.setIdentifier(identifier
                                .getIdentifierType(), identifier);
                } else // if the identifier type is on the primary entity
                // update primary entity with fix identifier value
                // end if
                if (primaryEntity.getIdentifier(identifier.getIdentifierType()) != null)
                    primaryEntity.setIdentifier(identifier.getIdentifierType(),
                            identifier);
                else {
                    // add the new entity identity from the fix identifier to
                    // the primary entity
                    primaryEntity.addIdentifier(identifier);
                }
            }
        }
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

    private boolean conflicts(Identifier id, Set<Identifier> identifiers) {
        for (Identifier identifier : identifiers)
            if (id.getIdentifierType().equals(identifier.getIdentifierType())
                    && !id.equals(identifier))
                return true;
        return false;
    }

}
package moten.david.ets.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import moten.david.ets.client.model.Identity;
import moten.david.ets.client.model.MyEntity;
import moten.david.ets.client.model.MyParent;
import moten.david.matchstack.Merger;
import moten.david.matchstack.Util;
import moten.david.matchstack.Merger.MergeResult;
import moten.david.matchstack.types.Identifier;
import moten.david.matchstack.types.TimedIdentifier;
import moten.david.matchstack.types.impl.MyIdentifier;
import moten.david.matchstack.types.impl.MyIdentifierType;
import moten.david.matchstack.types.impl.MyTimedIdentifier;
import moten.david.util.appengine.LockManager;
import moten.david.util.collections.CollectionsUtil;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.appengine.repackaged.com.google.common.collect.Sets;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.vercer.engine.persist.ObjectDatastore;

/**
 * Stores fixes in a google app engine datastore using the Matchstack merge
 * engine from moten-util.
 * 
 * @author dxm
 */
public class EntitiesGae implements Entities {

    private static final int LOCK_TIMEOUT = 30000;
    private static final Logger log = Logger.getLogger(EntitiesGae.class
            .getName());
    /**
     * Twig datastore.
     */
    private final ObjectDatastore datastore;
    /**
     * The merging engine.
     */
    private final Merger merger;
    /**
     * Lock manager
     */
    private final LockManager lockManager;

    /**
     * Constructor.
     * 
     * @param datastore
     * @param merger
     */
    @Inject
    public EntitiesGae(ObjectDatastore datastore, Merger merger,
            LockManager lockManager) {
        this.datastore = datastore;
        this.merger = merger;
        this.lockManager = lockManager;
    }

    @Override
    public void clearAll() {
        datastore.deleteAll(Identity.class);
        datastore.deleteAll(MyEntity.class);
        datastore.deleteAll(MyParent.class);
        log.info("deleted all entities");
    }

    @Override
    public void add(final Iterable<MyFix> fixes) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                try {
                    // start the transaction
                    datastore.beginTransaction();

                    // add all the fixes
                    for (MyFix fix : fixes)
                        add(fix);

                    // commit the transaction
                    datastore.getTransaction().commit();
                } catch (RuntimeException e) {
                    // if an error occurs log the exception and rollback the
                    // transaction
                    logExceptionAndRollback(e);
                    throw e;
                }
            }
        };
        lockManager.performWithLock("addFix", runnable, LOCK_TIMEOUT);
    }

    /**
     * Logs an exception and rolllback the current transaction if it is active.
     * 
     * @param e
     */
    private void logExceptionAndRollback(RuntimeException e) {
        log.log(Level.SEVERE, e.getMessage(), e);
        log.info("rolling back");
        if (datastore.getTransaction().isActive())
            datastore.getTransaction().rollback();
        log.info("rolled back");
    }

    /**
     * Adds a single fix to the datastore.
     * 
     * @param fix
     */
    public synchronized void add(MyFix fix) {

        // ensure parent is stored and the field initialized
        MyParent parent = getParent(datastore, "main");

        // create timed identifiers from the fix identifiers
        log.info("creating set of TimedIdentifier from fix");
        Set<TimedIdentifier> fixIds = createTimedIdentifierSet(fix);

        // find intersections of fix with existing identifiers
        log.info("finding intersections");
        Set<Set<Identity>> intersectingIdentities = findIntersectingIdentities(
                parent, fixIds);
        // record entity ids against identifiers
        Map<Identifier, Long> identifierEntityIds = recordEntityIdsByIdentifier(intersectingIdentities);
        // convert to TimedIdentifiers
        Set<Set<TimedIdentifier>> intersecting = ImmutableSet
                .copyOf(Collections2.transform(intersectingIdentities,
                        identitySetToTimedIdentifierSet));
        log.info("intersecting=" + intersecting);

        // calculate the merges of the fix with all intersecting entities
        log.info("merging");
        MergeResult merge = merger.merge(fixIds, intersecting);

        // if none of the identifiers in the fix matched an existing entity
        // then create a new one
        if (merge.getPmza().size() == 0)
            storeNewEntity(parent, fix, fixIds);
        else
            mergeWithDatastore(parent, fix, merge, identifierEntityIds);

        log.info("fix added");

    }

    /**
     * Returns a map of the entity ids by {@link Identifier}
     * 
     * @param intersectingIdentities
     * @return
     */
    private Map<Identifier, Long> recordEntityIdsByIdentifier(
            Set<Set<Identity>> intersectingIdentities) {
        HashMap<Identifier, Long> map = new HashMap<Identifier, Long>();
        for (Set<Identity> set : intersectingIdentities) {
            for (Identity identity : set) {
                // convert Identity to TimedIdentifier
                TimedIdentifier ti = identityToTimedIdentifier.apply(identity);
                // record the identifier entity ids
                map.put(ti.getIdentifier(), identity.getEntityId());
            }
        }
        return map;
    }

    /**
     * Merges a fix with the datastore attached to the given parent.
     * 
     * @param parent
     * @param fix
     * @param merge
     * @param identifierEntityIds
     */
    private void mergeWithDatastore(MyParent parent, MyFix fix,
            MergeResult merge, Map<Identifier, Long> identifierEntityIds) {
        log.info("intersection not empty");
        // the merge result set that intersects identifier wise with
        // pmza will get the entity id of pmza. all other merge result
        // sets will be given the entity of the set in intersections
        // that intersects with it.

        log.info("merge.pmza=" + merge.getPmza());
        log.info("merge.merged=" + merge.getMerged());

        // find the result of the merge on pmza (assign it to pmzaNew)
        Set<TimedIdentifier> pmzaNew = null;
        Set<Identifier> pmzaIds = Util.ids(merge.getPmza());
        for (Set<TimedIdentifier> set : merge.getMerged()) {
            if (CollectionsUtil.intersect(Util.ids(set), pmzaIds)) {
                pmzaNew = set;
            }
        }
        Preconditions.checkNotNull(pmzaNew, "pmzaNew should not be null");

        // for all merged sets create and store identity objects
        for (Set<TimedIdentifier> set : merge.getMerged()) {
            for (TimedIdentifier ti : set) {
                Identity identity = createIdentity(ti);
                identity.setEntityId(identifierEntityIds
                        .get(ti.getIdentifier()));
                datastore.storeOrUpdate(identity, parent);
            }
        }

        // override the primary match merged set with the entity
        // corresponding to the non-merged primary match
        Long pmzaEntityId = identifierEntityIds.get(merge.getPmza().iterator()
                .next().getIdentifier());
        log.info("pmza entity id=" + pmzaEntityId);
        for (TimedIdentifier ti : pmzaNew) {
            Identity identity = createIdentity(ti);
            identity.setEntityId(pmzaEntityId);
            datastore.storeOrUpdate(identity, parent);
        }

        // load the pmza entity so that we can update the latest fix
        // info
        MyEntity entity = datastore.load(MyEntity.class, pmzaEntityId, parent);
        entity.setLatestFix(fix.getFix());
        datastore.update(entity);
    }

    /**
     * Returns the common Parent object (all objects involved in a datastore
     * transaction must be children of the same parent.
     * 
     * @return
     */
    public static MyParent getParent(ObjectDatastore datastore, String name) {
        log.info("loading parent");
        MyParent parent = datastore.load(MyParent.class, name);
        if (parent == null) {
            log.info("storing parent");
            parent = new MyParent();
            parent.setName("main");
            datastore.store(parent);
            log.info("stored parent");
        }
        return parent;
    }

    /**
     * Stores a new entity given by a fix.
     * 
     * @param parent
     * @param fix
     * @param fixIds
     */
    private void storeNewEntity(MyParent parent, MyFix fix,
            Set<TimedIdentifier> fixIds) {
        log.info("storing new identity");
        MyEntity entity = new MyEntity();
        entity.setId(System.currentTimeMillis());
        entity.setLatestFix(fix.getFix());
        entity.setType("vessel");
        datastore.store(entity, parent);
        log.info("stored new entity");
        for (TimedIdentifier ti : fixIds) {
            Identity identity = createIdentity(ti);
            identity.setEntityId(entity.getId());
            datastore.store(identity, parent);
        }
    }

    /**
     * Returns the type name of a {@link TimedIdentifier} (assumes it is an
     * instance of {@link MyTimedIdentifier}.
     * 
     * @param ti
     * @return
     */
    private String getTypeName(TimedIdentifier ti) {
        MyIdentifierType type = (MyIdentifierType) ((MyTimedIdentifier) ti)
                .getIdentifier().getIdentifierType();
        return type.getName();
    }

    /**
     * Returns the value field of a {@link TimedIdentifier} (assumes it is an
     * instance of {@link MyTimedIdentifier}.
     * 
     * @param ti
     * @return
     */
    private String getTypeValue(TimedIdentifier ti) {
        return ((MyTimedIdentifier) ti).getIdentifier().getValue();
    }

    /**
     * Creates an {@link Identity} from a {@link TimedIdentifier}.
     * 
     * @param ti
     * @return
     */
    private Identity createIdentity(TimedIdentifier ti) {
        Identity identity = new Identity();
        identity.setId(getIdentityId(ti));
        identity.setName(getTypeName(ti));
        identity.setValue(getTypeValue(ti));
        identity.setTime(new Date(ti.getTime()));
        return identity;
    }

    /**
     * Returns a string representation of the identifier part of a timed
     * identifier.
     * 
     * @param ti
     * @return
     */
    private String getIdentityId(TimedIdentifier ti) {
        return getTypeName(ti) + ":" + getTypeValue(ti);
    }

    /**
     * Returns all intersecting identities in terms of the identifier type and
     * value from a set of {@link TimedIdentifer}.
     * 
     * @param parent
     * @param ids
     * @return
     */
    private Set<Set<Identity>> findIntersectingIdentities(MyParent parent,
            Set<TimedIdentifier> ids) {

        Set<Long> entityIdsUsed = Sets.newHashSet();
        com.google.common.collect.ImmutableList.Builder<Future<QueryResultIterator<Identity>>> futures = ImmutableList
                .builder();
        for (TimedIdentifier ti : ids) {
            String id = getIdentityId(ti);
            log.info("searching for Identity " + id);

            // search for the identity using the value field
            Iterator<Identity> iterator = datastore.find().type(Identity.class)
                    .addFilter("value", Query.FilterOperator.EQUAL,
                            getTypeValue(ti)).withAncestor(parent)
                    .returnResultsNow();

            // get the name of the timed identifier for filtering
            final String typeName = getTypeName(ti);

            // refine the search using the identifier type name
            iterator = filterByTypeName(iterator, typeName);

            // ensure only one item returned (null means not found)
            Identity identity = Iterators.getOnlyElement(iterator, null);

            if (identity == null)
                // not intersecting
                log.info(id + " not found");
            else {
                Preconditions.checkNotNull(identity.getEntityId(),
                        "identity entity should not be null");
                if (!entityIdsUsed.contains(identity.getEntityId())) {
                    futures.add(startEntityIdentitiesQuery(parent, identity
                            .getEntityId()));
                }
                entityIdsUsed.add(identity.getEntityId());
            }
        }
        // get the results of the async queries
        return getIdentities(futures);

    }

    /**
     * Returns the {@link Future} for a query that returns all the identities
     * used by an entity.
     * 
     * @param parent
     * @param entityId
     * @return
     */
    private Future<QueryResultIterator<Identity>> startEntityIdentitiesQuery(
            MyParent parent, Long entityId) {
        // find all identities of the intersecting asynchronously
        Future<QueryResultIterator<Identity>> future = datastore.find().type(
                Identity.class).addFilter("entityId",
                Query.FilterOperator.EQUAL, entityId).withAncestor(parent)
                .returnResultsLater();
        return future;
    }

    /**
     * Returns identities from the futures of the already initiated async
     * queries.
     * 
     * @param futures
     * @return
     */
    private Set<Set<Identity>> getIdentities(
            com.google.common.collect.ImmutableList.Builder<Future<QueryResultIterator<Identity>>> futures) {
        Builder<Set<Identity>> builder = ImmutableSet.builder();
        for (Future<QueryResultIterator<Identity>> future : futures.build()) {
            try {
                QueryResultIterator<Identity> it = future.get();
                Set<Identity> set = ImmutableSet.copyOf(it);
                builder.add(set);
            } catch (InterruptedException e) {
                // if interrupted exit by throwing an exception
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }

    /**
     * Returns an iterator which is filtered for type Name equals
     * <code>typeName</code>.
     * 
     * @param iterator
     * @param typeName
     * @return
     */
    private Iterator<Identity> filterByTypeName(Iterator<Identity> iterator,
            final String typeName) {
        return Iterators.filter(iterator,
                new com.google.common.base.Predicate<Identity>() {

                    @Override
                    public boolean apply(Identity identity) {
                        return typeName.equals(identity.getName());
                    }
                });
    }

    /**
     * Converts an {@link Identity} to a {@link TimedIdentifier}
     */
    private static Function<? super Identity, TimedIdentifier> identityToTimedIdentifier = new Function<Identity, TimedIdentifier>() {
        @Override
        public TimedIdentifier apply(Identity i) {
            MyIdentifierType type = new MyIdentifierType(i.getName(), 1.0);
            MyIdentifier id = new MyIdentifier(type, i.getValue());
            MyTimedIdentifier ti = new MyTimedIdentifier(id, i.getTime()
                    .getTime());
            return ti;
        }
    };

    /**
     * Converts a set of {@link Identity} to a set of {@link TimedIdentifier}
     */
    private static Function<Set<Identity>, Set<TimedIdentifier>> identitySetToTimedIdentifierSet = new Function<Set<Identity>, Set<TimedIdentifier>>() {

        @Override
        public Set<TimedIdentifier> apply(Set<Identity> set) {
            return ImmutableSet.copyOf(Collections2.transform(set,
                    identityToTimedIdentifier));
        }

    };

    /**
     * Creates a set of {@link TimedIdentifier} corresponding to the identifiers
     * of <code>fix</code> with the time of <code>fix</code>.
     * 
     * @param fix
     * @return
     */
    private Set<TimedIdentifier> createTimedIdentifierSet(MyFix fix) {
        Builder<TimedIdentifier> builder = ImmutableSet.builder();
        for (String name : fix.getIds().keySet()) {
            String value = fix.getIds().get(name);
            MyIdentifierType type = new MyIdentifierType(name, 1.0);
            MyIdentifier id = new MyIdentifier(type, value);
            MyTimedIdentifier ti = new MyTimedIdentifier(id, fix.getFix()
                    .getTime().getTime());
            builder.add(ti);
        }
        return builder.build();
    }

}

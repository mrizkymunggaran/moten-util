package moten.david.ets.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import moten.david.util.collections.CollectionsUtil;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.appengine.repackaged.com.google.common.collect.Sets;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.vercer.engine.persist.ObjectDatastore;

public class EntitiesGae implements Entities {

    private final ObjectDatastore datastore;
    private final Merger merger;
    private MyParent parent;

    @Inject
    public EntitiesGae(ObjectDatastore datastore, Merger merger) {
        this.datastore = datastore;
        this.merger = merger;
    }

    @Override
    public void clearAll() {
        datastore.deleteAll(Identity.class);
        datastore.deleteAll(MyEntity.class);
        datastore.deleteAll(MyParent.class);
    }

    @Override
    public void add(MyFix fix) {
        try {
            checkParent();

            // start the transaction
            datastore.beginTransaction();

            // create timed identifiers from the fix identifiers
            log("creating set of TimedIdentifier from fix");
            Set<TimedIdentifier> fixIds = createTimedIdentifierSet(fix);

            // find intersections of fix with existing identifiers
            log("finding intersections");
            Set<Set<Identity>> intersectingIdentities = findIntersectingIdentities(fixIds);
            // record entity ids against identifiers
            Map<Identifier, Long> identifierEntityIds = recordEntityIdsByIdentifier(intersectingIdentities);
            // convert to TimedIdentifiers
            Set<Set<TimedIdentifier>> intersecting = ImmutableSet
                    .copyOf(Collections2.transform(intersectingIdentities,
                            identitySetToTimedIdentifierSet));
            log("intersecting=" + intersecting);

            // calculate the merges of the fix with all intersecting entities
            log("merging");
            MergeResult merge = merger.merge(fixIds, intersecting);

            // if none of the identifiers in the fix matched an existing entity
            // then create a new one
            if (merge.getPmza().size() == 0)
                storeNewEntity(fix, fixIds);
            else
                mergeWithDatastore(fix, merge, identifierEntityIds);

            // commit the transaction
            datastore.getTransaction().commit();
            log("fix added");
        } catch (RuntimeException e) {
            log(e.getMessage());
            e.printStackTrace();
            datastore.getTransaction().rollback();
            throw e;
        }
    }

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

    private void mergeWithDatastore(MyFix fix, MergeResult merge,
            Map<Identifier, Long> identifierEntityIds) {
        log("intersection not empty");
        // the merge result set that intersects identifier wise with
        // pmza will get the entity id of pmza. all other merge result
        // sets will be given the entity of the set in intersections
        // that intersects with it.

        log("merge.pmza=" + merge.getPmza());
        log("merge.merged=" + merge.getMerged());

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
        log("pmza entity id=" + pmzaEntityId);
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

    private synchronized void checkParent() {
        if (parent == null)
            parent = getParent();
    }

    private MyParent getParent() {
        log("loading parent");
        MyParent parent = datastore.load(MyParent.class, "main");
        if (parent == null) {
            log("storing parent");
            parent = new MyParent();
            parent.setName("main");
            datastore.store(parent);
            log("stored parent");
        }
        return parent;
    }

    private void storeNewEntity(MyFix fix, Set<TimedIdentifier> fixIds) {
        log("storing new identity");
        MyEntity entity = new MyEntity();
        entity.setId(System.currentTimeMillis());
        entity.setLatestFix(fix.getFix());
        entity.setType("vessel");
        datastore.store(entity, parent);
        log("stored new entity");
        for (TimedIdentifier ti : fixIds) {
            Identity identity = createIdentity(ti);
            identity.setEntityId(entity.getId());
            datastore.store(identity, parent);
        }
    }

    private String getTypeName(TimedIdentifier ti) {
        MyIdentifierType type = (MyIdentifierType) ((MyTimedIdentifier) ti)
                .getIdentifier().getIdentifierType();
        return type.getName();
    }

    private String getTypeValue(TimedIdentifier ti) {
        return ((MyTimedIdentifier) ti).getIdentifier().getValue();
    }

    private Identity createIdentity(TimedIdentifier ti) {
        Identity identity = new Identity();
        identity.setId(getIdentityId(ti));
        identity.setName(getTypeName(ti));
        identity.setValue(getTypeValue(ti));
        identity.setTime(new Date(ti.getTime()));
        return identity;
    }

    private String getIdentityId(TimedIdentifier ti) {
        return getTypeName(ti) + ":" + getTypeValue(ti);
    }

    private void log(String string) {
        System.out.println(string);
    }

    private Set<Set<Identity>> findIntersectingIdentities(
            Set<TimedIdentifier> ids) {
        Builder<Set<Identity>> builder = ImmutableSet.builder();
        Set<Long> entityIdsUsed = Sets.newHashSet();
        for (TimedIdentifier ti : ids) {
            String id = getIdentityId(ti);
            log("searching for Identity " + id);
            Identity identity = getSingleResult(datastore.find().type(
                    Identity.class).addFilter("value",
                    Query.FilterOperator.EQUAL, getTypeValue(ti)).withAncestor(
                    parent).addFilter("name", FilterOperator.EQUAL,
                    getTypeName(ti)).returnResultsNow());

            if (identity == null)
                log(id + " not found");
            else {
                Preconditions.checkNotNull(identity.getEntityId(),
                        "identity entity should not be null");
                if (!entityIdsUsed.contains(identity.getEntityId())) {
                    Future<QueryResultIterator<Identity>> future = datastore
                            .find().type(Identity.class).addFilter("entityId",
                                    Query.FilterOperator.EQUAL,
                                    identity.getEntityId())
                            .withAncestor(parent).returnResultsLater();
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
                entityIdsUsed.add(identity.getEntityId());
            }
        }
        return builder.build();
    }

    private <T> T getSingleResult(Iterator<T> it) {
        Preconditions.checkNotNull(it, "iterator cannot be null");
        if (it.hasNext()) {
            T result = it.next();
            if (it.hasNext())
                throw new RuntimeException(
                        "expected only one result and query returned at least two");
            else
                return result;
        } else
            return null;
    }

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

    private static Function<Set<Identity>, Set<TimedIdentifier>> identitySetToTimedIdentifierSet = new Function<Set<Identity>, Set<TimedIdentifier>>() {

        @Override
        public Set<TimedIdentifier> apply(Set<Identity> set) {
            return ImmutableSet.copyOf(Collections2.transform(set,
                    identityToTimedIdentifier));
        }

    };

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

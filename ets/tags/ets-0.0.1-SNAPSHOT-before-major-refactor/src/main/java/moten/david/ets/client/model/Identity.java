package moten.david.ets.client.model;

import java.util.Date;

import com.vercer.engine.persist.annotation.Key;

/**
 * Identifying key value pair for a {@link MyEntity}.
 * 
 * @author dxm
 */
public class Identity {

    @Key
    String id;
    String name;
    String value;
    String entityId;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

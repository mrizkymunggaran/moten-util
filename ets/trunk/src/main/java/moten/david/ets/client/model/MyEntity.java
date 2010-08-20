package moten.david.ets.client.model;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

/**
 * An entity which includes a type, geoHash and latest position (fix).
 * 
 * @author dxm
 */
public class MyEntity {
    @Key
    String id;
    String type;
    String geoHash;
    @Embed
    Fix latestFix;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public Fix getLatestFix() {
        return latestFix;
    }

    public void setLatestFix(Fix latestFix) {
        this.latestFix = latestFix;
    }
}

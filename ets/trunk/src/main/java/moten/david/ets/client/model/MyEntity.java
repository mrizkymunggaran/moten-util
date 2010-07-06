package moten.david.ets.client.model;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

public class MyEntity {
    @Key
    long id;
    String type;
    String geoHash;
    @Embed
    Fix latestFix;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

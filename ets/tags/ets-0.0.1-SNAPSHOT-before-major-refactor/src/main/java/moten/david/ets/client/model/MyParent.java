package moten.david.ets.client.model;

import com.vercer.engine.persist.annotation.Key;

/**
 * Parent (grouping) object.
 * 
 * @author dxm
 */
public class MyParent {
    @Key
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

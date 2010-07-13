package moten.david.ets.server;

import java.util.Map;

import moten.david.ets.client.model.Fix;

/**
 * Fix plus identities.
 * 
 * @author dave
 * 
 */
public class MyFix {
    private final Fix fix;

    private final Map<String, String> ids;

    /**
     * Constructor.
     * 
     * @param fix
     * @param ids
     */
    public MyFix(Fix fix, Map<String, String> ids) {
        super();
        this.fix = fix;
        this.ids = ids;
    }

    /**
     * Returns the identifiers reported with the fix.
     * 
     * @return
     */
    public Map<String, String> getIds() {
        return ids;
    }

    /**
     * Returns the position part of the fix.
     * 
     * @return
     */
    public Fix getFix() {
        return fix;
    }

    @Override
    public String toString() {
        return "MyFix [fix=" + fix + ", ids=" + ids + "]";
    }

}

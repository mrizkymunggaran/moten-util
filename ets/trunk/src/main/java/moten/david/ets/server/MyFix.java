package moten.david.ets.server;

import java.util.Map;

import moten.david.ets.client.model.Fix;

public class MyFix {
    private final Fix fix;

    @Override
    public String toString() {
        return "MyFix [fix=" + fix + ", ids=" + ids + "]";
    }

    private final Map<String, String> ids;

    public Map<String, String> getIds() {
        return ids;
    }

    public Fix getFix() {
        return fix;
    }

    public MyFix(Fix fix, Map<String, String> ids) {
        super();
        this.fix = fix;
        this.ids = ids;
    }

}

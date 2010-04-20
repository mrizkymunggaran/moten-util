package moten.david.imatch.memory;

import moten.david.imatch.TimedIdentifier;

public class MyTimedIdentifier implements TimedIdentifier {

    private final long time;
    private final MyIdentifier identifier;

    public MyTimedIdentifier(MyIdentifier identifier, long time) {
        this.identifier = identifier;
        this.time = time;
    }

    @Override
    public MyIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public long getTime() {
        return time;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return identifier.toString();
    }

}

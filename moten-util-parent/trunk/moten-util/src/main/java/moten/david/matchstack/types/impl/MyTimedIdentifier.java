package moten.david.matchstack.types.impl;

import moten.david.matchstack.types.TimedIdentifier;

/**
 * Implementation of a {@link TimedIdentifier}.
 * 
 * @author dave
 * 
 */
public class MyTimedIdentifier implements TimedIdentifier {

    private final long time;
    private final MyIdentifier identifier;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyTimedIdentifier other = (MyTimedIdentifier) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (time != other.time)
            return false;
        return true;
    }

    /**
     * Constructor.
     * 
     * @param identifier
     * @param time
     */
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
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return identifier.toString() + "(" + time + ")";
    }

}

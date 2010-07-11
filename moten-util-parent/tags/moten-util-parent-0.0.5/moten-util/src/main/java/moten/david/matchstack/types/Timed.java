package moten.david.matchstack.types;

/**
 * Times something.
 * 
 * @author dave
 * 
 */
public interface Timed {
    /**
     * Returns the time as number of millis since 00:00 1 Jan 1970 UTC.
     * 
     * @return
     */
    long getTime();
}

package moten.david.util.expression;

/**
 * Standard time units.
 * 
 * @author dxm
 * 
 */
public enum DurationType {
    MILLIS(1), SECOND(1000l), MINUTE(60 * 1000l), HOUR(3600 * 1000l), DAY(
            24 * 3600 * 1000l), WEEK(7 * 24 * 3600l * 1000l);
    private long factor;

    private DurationType(long factor) {
        this.factor = factor;
    }

    public long getFactor() {
        return factor;
    }

}

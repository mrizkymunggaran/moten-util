package moten.david.ete.memory;

import java.math.BigDecimal;

/**
 * Velocity (speed and course). Must implement equals and hashcode because MyFix
 * compares Velocity values in it's equals and hashcode.
 * 
 * @author dxm
 */
public class Velocity {

    private final BigDecimal courseDegrees;
    private final BigDecimal speedMetresPerSecond;

    public Velocity(BigDecimal courseDegrees, BigDecimal speedMetresPerSecond) {
        super();
        this.courseDegrees = courseDegrees;
        this.speedMetresPerSecond = speedMetresPerSecond;
    }

    public BigDecimal getCourseDegrees() {
        return courseDegrees;
    }

    public BigDecimal getSpeedMetresPerSecond() {
        return speedMetresPerSecond;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((courseDegrees == null) ? 0 : courseDegrees.hashCode());
        result = prime
                * result
                + ((speedMetresPerSecond == null) ? 0 : speedMetresPerSecond
                        .hashCode());
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
        Velocity other = (Velocity) obj;
        if (courseDegrees == null) {
            if (other.courseDegrees != null)
                return false;
        } else if (!courseDegrees.equals(other.courseDegrees))
            return false;
        if (speedMetresPerSecond == null) {
            if (other.speedMetresPerSecond != null)
                return false;
        } else if (!speedMetresPerSecond.equals(other.speedMetresPerSecond))
            return false;
        return true;
    }

}

package moten.david.util.navigation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dxm
 *
 */
/**
 * @author dxm
 * 
 */
public class Position {
    private final double lat;
    private final double lon;
    private final double alt;
    private static double radiusEarthKm = 6371.01;
    private static double circumferenceEarthKm = 2.0 * Math.PI * radiusEarthKm;

    /**
     * @param lat
     *            in degrees
     * @param lon
     *            in degrees
     */
    public Position(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.alt = 0.0;
    }

    /**
     * @param lat
     *            in degrees
     * @param lon
     *            in degrees
     * @param alt
     *            in metres
     */
    public Position(double lat, double lon, double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    @Override
    public String toString() {
        return "[" + lat + "," + lon + "]";
    }

    /**
     * Predicts position travelling along a great circle arc based on the
     * Haversine formula.
     * 
     * From http://www.movable-type.co.uk/scripts/latlong.html
     * 
     * @param distanceKm
     * @param courseDegrees
     * @return
     */
    public Position predict(double distanceKm, double courseDegrees) {
        assrt(alt == 0.0, "Predictions only valid for Earth's surface");
        double dr = distanceKm / radiusEarthKm;
        double latR = Math.toRadians(lat);
        double lonR = Math.toRadians(lon);
        double courseR = Math.toRadians(courseDegrees);
        double lat2Radians = Math.asin(Math.sin(latR) * Math.cos(dr)
                + Math.cos(latR) * Math.sin(dr) * Math.cos(courseR));
        double lon2Radians = Math.atan2(Math.sin(courseR) * Math.sin(dr)
                * Math.cos(latR), Math.cos(dr) - Math.sin(latR)
                * Math.sin(lat2Radians));
        double lon3Radians = mod(lonR + lon2Radians + Math.PI, 2 * Math.PI)
                - Math.PI;
        return new Position(Math.toDegrees(lat2Radians), Math
                .toDegrees(lon3Radians));
    }

    public static double toDegrees(double degrees, double minutes,
            double seconds) {
        return degrees + minutes / 60.0 + seconds / 3600.0;
    }

    private double sqr(double d) {
        return d * d;
    }

    /**
     * Return an array of Positions representing the earths limb (aka: horizon)
     * as viewed from this Position in space. This position must have altitude >
     * 0
     * 
     * The array returned will have the specified number of elements (radials).
     * 
     * 
     * This method is useful for the calculation of satellite footprints or the
     * position of the Earth's day/night terminator.
     * 
     * 
     * This formula from Aviation Formula by Ed Williams
     * (http://williams.best.vwh.net/avform.htm)
     * 
     * @param radials
     *            the number of radials to calculated (evenly spaced around the
     *            circumference of the circle
     * 
     * @return An array of radial points a fixed distance from this point
     *         representing the Earth's limb as viewed from this point in space.
     * 
     */
    public Position[] getEarthLimb(int radials) {

        Position[] result = new Position[radials];

        double radialDegrees = 0.0;
        double incDegrees = 360.0 / radials;
        double quarterEarthKm = circumferenceEarthKm / 4.0;
        Position surfacePosition = new Position(this.lat, this.lon, 0.0);

        // Assert( this.alt>0.0, "getEarthLimb() requires Position a positive
        // altitude");
        for (int i = 0; i < radials; i++) {

            // TODO: base the distance on the altitude above the Earth

            result[i] = surfacePosition.predict(quarterEarthKm, radialDegrees);
            radialDegrees += incDegrees;
        }

        return result;
    }

    /**
     * returns distance between two WGS84 positions according to Vincenty's
     * formula from Wikipedia
     * 
     * @param position
     * @return
     */
    public double getDistanceToKm(Position position) {
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(position.lat);
        double lon1 = Math.toRadians(lon);
        double lon2 = Math.toRadians(position.lon);
        double deltaLon = lon2 - lon1;
        double top = Math.sqrt(sqr(Math.cos(lat2) * Math.sin(deltaLon))
                + sqr(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                        * Math.cos(lat2) * Math.cos(deltaLon)));
        double bottom = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(deltaLon);
        double distance = radiusEarthKm * Math.atan2(top, bottom);
        return Math.abs(distance);
    }

    public double getBearingDegrees(Position position) {
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(position.lat);
        double lon1 = Math.toRadians(lon);
        double lon2 = Math.toRadians(position.lon);
        double dLon = lon2 - lon1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double course = Math.toDegrees(Math.atan2(y, x));
        if (course < 0)
            course += 360;
        return course;
    }

    /**
     * returns difference in degrees in the range -180 to 180
     * 
     * @param bearing1
     *            degrees between -360 and 360
     * @param bearing2
     *            degrees between -360 and 360
     * @return
     */
    public static double getBearingDifferenceDegrees(double bearing1,
            double bearing2) {
        if (bearing1 < 0)
            bearing1 += 360;
        if (bearing2 > 180)
            bearing2 -= 360;
        double result = bearing1 - bearing2;
        if (result > 180)
            result -= 360;
        return result;
    }

    /**
     * calculates the distance of a point to the great circle path between p1
     * and p2.
     * 
     * Formula from: http://www.movable-type.co.uk/scripts/latlong.html
     * 
     * @param p1
     * @param p2
     * @return
     */
    public double getDistanceKmToPath(Position p1, Position p2) {
        double d = radiusEarthKm
                * Math.asin(Math.sin(getDistanceToKm(p1) / radiusEarthKm)
                        * Math.sin(Math.toRadians(getBearingDegrees(p1)
                                - p1.getBearingDegrees(p2))));
        return Math.abs(d);
    }

    public static String toDegreesMinutesDecimalMinutesLatitude(double lat) {
        long degrees = Math.round(Math.signum(lat) * Math.floor(Math.abs(lat)));
        double remaining = Math.abs(lat - degrees);
        remaining *= 60;
        String result = Math.abs(degrees) + "" + (char) 0x00B0
                + new DecimalFormat("00.00").format(remaining) + "'"
                + (lat < 0 ? "S" : "N");
        return result;
    }

    public static String toDegreesMinutesDecimalMinutesLongitude(double lon) {
        long degrees = Math.round(Math.signum(lon) * Math.floor(Math.abs(lon)));
        double remaining = Math.abs(lon - degrees);
        remaining *= 60;
        String result = Math.abs(degrees) + "" + (char) 0x00B0
                + new DecimalFormat("00.00").format(remaining) + "'"
                + (lon < 0 ? "W" : "E");
        return result;
    }

    private static double mod(double y, double x) {

        x = Math.abs(x);
        int n = (int) (y / x);
        double mod = y - x * n;
        if (mod < 0) {
            mod += x;
        }
        return mod;
    }

    public static void assrt(boolean assertion, String msg) {
        if (!assertion == true)
            throw new RuntimeException("Assertion failed: " + msg);

    }

    /**
     * Returns a position along a path according to the proportion value
     * 
     * @param position
     * @param proportion
     *            is between 0 and 1 inclusive
     * @return
     */

    public Position getPositionAlongPath(Position position, double proportion) {

        if (proportion >= 0 && proportion <= 1) {

            // Get bearing degrees for course
            double courseDegrees = this.getBearingDegrees(position);

            // Get distance from position arg and this objects location
            double distanceKm = this.getDistanceToKm(position);

            // Predict the position for a proportion of the course
            // where this object is the start position and the arg
            // is the destination position.
            Position retPosition = this.predict(proportion * distanceKm,
                    courseDegrees);

            return retPosition;
        } else
            throw new RuntimeException(
                    "Proportion must be between 0 and 1 inclusive");
    }

    public List<Position> getPositionsAlongPath(Position position,
            double maxSegmentLengthKm) {

        // Get distance from this to position
        double distanceKm = this.getDistanceToKm(position);

        List<Position> positions = new ArrayList<Position>();

        long numSegments = Math.round(Math.floor(distanceKm
                / maxSegmentLengthKm)) + 1;
        positions.add(this);
        for (int i = 1; i < numSegments; i++)
            positions.add(getPositionAlongPath(position, i
                    / (double) numSegments));
        positions.add(position);
        return positions;
    }

    public Position to360() {
        double lat = this.lat;
        double lon = this.lon;
        if (lon < 0)
            lon += 360;
        return new Position(lat, lon);
    }

    /**
     * normalize the lat lon values of this to ensure that no large longitude
     * jumps are made from lastPosition (e.g. 179 to -180)
     * 
     * @param lastPosition
     */
    public Position ensureContinuous(Position lastPosition) {
        double lon = this.lon;
        if (Math.abs(lon - lastPosition.lon) > 180) {
            if (lastPosition.lon < 0)
                lon -= 360;
            else
                lon += 360;
            return new Position(lat, lon);
        } else
            return this;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        else if (o instanceof Position) {
            Position p = (Position) o;
            return p.lat == lat && p.lon == lon;
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return (int) (lat + lon);
    }
}

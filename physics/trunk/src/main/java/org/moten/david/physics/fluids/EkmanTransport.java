package org.moten.david.physics.fluids;

public class EkmanTransport {

	// All units are the SI units for the measure unless otherwise stated

	private static final double DENSITY_OF_AIR = 1.3;

	private static final double ROTATION_RATE_OF_EARTH_RADS_PER_SEC = 0.000072921;

	/**
	 * <p>
	 * Returns wind stress calculated according to <a href=
	 * "http://oceanworld.tamu.edu/resources/ocng_textbook/chapter04/chapter04_05.htm"
	 * >here</a>
	 * </p>
	 * 
	 * @param windSpeedAt10Metres
	 * @return
	 */
	private double getWindStress(double windSpeedAt10Metres) {
		return DENSITY_OF_AIR * getDragCoefficient(windSpeedAt10Metres)
				* sq(windSpeedAt10Metres);
	}

	/**
	 * <p>
	 * Returns drag coefficient calculated according to <a href=
	 * "http://oceanworld.tamu.edu/resources/ocng_textbook/chapter04/chapter04_05.htm"
	 * >here</a> using the Smith 1980 formula.
	 * </p>
	 * 
	 * @param windSpeedAt10Metres
	 * @return
	 */
	private double getDragCoefficient(double windSpeedAt10Metres) {
		// Smith 1980 formula
		return 0.44 + 0.063 * windSpeedAt10Metres;
	}

	private double getEddyViscosity() {
		return 1.0e-05;
	}

	private void calculateEkmanTransport(double ekmanDepth,
			double coriolisParameter, double windSpeedAt10Metres,
			boolean isNorthernHemisphere, double waterDensity, double z,
			double[] result) {
		double a = Math.PI / ekmanDepth * z;
		double b = a + Math.PI / 4;
		double v0 = Math.sqrt(2 * Math.PI * getWindStress(windSpeedAt10Metres))
				/ ekmanDepth / coriolisParameter / waterDensity;
		int sign = (isNorthernHemisphere ? 1 : -1);
		double u = sign * v0 * Math.cos(b) * Math.exp(a);

		double v = v0 * Math.sin(b) * Math.exp(a);
		result[0] = u;
		result[1] = v;
	}

	private void calculateEkmanTransport(double latitudeDegrees,
			double windSpeedAt10Metres, double z, double[] result) {
		double ekmanDepth = getEkmanDepth(latitudeDegrees);
		double f = getCoriolisParameter(latitudeDegrees);
		// approximation based on depth
		double waterDensity = (z > 20 ? 1.05 : 0.95) * 1025;// in kg/m2
		calculateEkmanTransport(ekmanDepth, f, windSpeedAt10Metres,
				latitudeDegrees > 0, waterDensity, z, result);
	}

	private double getCoriolisParameter(double latitudeDegrees) {
		return 2 * ROTATION_RATE_OF_EARTH_RADS_PER_SEC
				* Math.sin(Math.toRadians(latitudeDegrees));
	}

	private double getEkmanDepth(double latitudeDegrees) {
		return Math.PI
				* Math.sqrt(2 * getEddyViscosity()
						/ Math.abs(getCoriolisParameter(latitudeDegrees)));
	}

	private static double sq(double d) {
		return d * d;
	}

}

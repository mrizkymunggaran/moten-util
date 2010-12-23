package moten.david.util.navigation;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

	@Test
	public void testPredict() {
		Position p = new Position(53, 3);
		Position p2 = p.predict(100, 30);
		Assert.assertEquals(53.77644258276322, p2.getLat(), 0.01);
		Assert.assertEquals(3.7609191005595877, p2.getLon(), 0.01);

		// large distance tests around the equator

		double meanCircumferenceKm = 40041.47;

		// start on equator, just East of Greenwich
		p = new Position(0, 3);

		// quarter circumference, Still in Eastern hemisphere

		p2 = p.predict(meanCircumferenceKm / 4.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(93.0, p2.getLon(), 0.1);

		// half circumference, Now in Western hemisphere,
		// just East of the International date line

		p2 = p.predict(meanCircumferenceKm / 2.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-177.0, p2.getLon(), 0.1);

		// three quarters circumference, Now in Western hemisphere,

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-87.0, p2.getLon(), 0.1);

		// full circumference, back to start
		// relax Longitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

		// same thing but backwards (heading west)

		// quarter circumference, Still in western hemisphere

		p2 = p.predict(meanCircumferenceKm / 4.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-87.0, p2.getLon(), 0.1);

		// half circumference, Now in Western hemisphere,
		// just East of the International date line

		p2 = p.predict(meanCircumferenceKm / 2.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-177.0, p2.getLon(), 0.1);

		// three quarters circumference, Now in eastern hemisphere,

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(93.0, p2.getLon(), 0.1);

		// full circumference, back to start
		// relax Longitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

		// OVER THE South POLE
		// ===================

		// quarter circumference, should be at south pole

		p2 = p.predict(meanCircumferenceKm / 4.0, 180);
		Assert.assertEquals(-90.0, p2.getLat(), 0.1);

		// this next assertion is by no means confident
		// expecting 3 but getting it's reciprocal.
		// Strange things happen at the pole!!

		Assert.assertEquals(-177, p2.getLon(), 0.00001);

		// half circumference, should be at the equator
		// but in the Western hemisphere

		p2 = p.predict(meanCircumferenceKm / 2.0, 180);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.1);
		Assert.assertEquals(-177, p2.getLon(), 0.00001);

		// 3/4 circumference, should be at the north Pole
		// but in the Western hemisphere

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 180);
		Assert.assertEquals(90.0, p2.getLat(), 0.1);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.00001);

		// full circumference, back to start
		// relax latitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.1);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

	}

	/**
	 * worked example from http://en.wikipedia.org/wiki/Great-circle_distance#
	 * Radius_for_spherical_Earth used for test below
	 */
	@Test
	public void testDistance() {

		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);
		Assert.assertEquals(2886.45, p1.getDistanceToKm(p2), 0.01);
	}

	@Test
	public void testBearing() {
		// test case taken from Geoscience Australia implementation of
		// Vincenty formula.
		// http://www.ga.gov.au/bin/gda_vincenty.cgi?inverse=0&lat_degrees1=-37&lat_minutes1=57&lat_seconds1=03.72030&NamePoint1=Flinders+Peak&lon_degrees1=144&lon_minutes1=25&lon_seconds1=29.52440&forward_azimuth_deg=306&forward_azimuth_min=52&forward_azimuth_sec=05.37&NamePoint2=Buninyong&ellipsoidal_dist=54972.217&lat_deg1=-37+deg&lat_min1=57+min&lat_sec1=3.7203+sec&lon_deg1=144+deg&lon_min1=25+min&lon_sec1=29.5244+sec&f_az_deg=306+deg&f_az_min=52+min&f_az_sec=5.37+sec&Submit=Submit+Data
		// Note that we are not using Vincenty formula but we are close to the
		// answer (within 0.2 degrees). That's sufficient!
		Position p1 = new Position(-(37 + 57.0 / 60 + 3.72030 / 3600), 144
				+ 25.0 / 60 + 29.52440 / 3600);
		Position p2 = new Position(-(37 + 39.0 / 60 + 10.15718 / 3600), 143
				+ 55.0 / 60 + 35.38564 / 3600);
		Assert.assertEquals(Position.toDegrees(306, 52, 5.37), p1
				.getBearingDegrees(p2), 0.2);
	}

	private String getDms(double d) {
		double d2 = Math.abs(d);
		long intPart = Math.round(Math.floor(d2));
		double minutes = (d2 - intPart) * 60;
		return intPart + "degs " + minutes + "minutes";
	}

	@Test
	public void testBearingDifference() {
		double precision = 0.00001;
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(20, 5),
				precision);
		Assert.assertEquals(15.0,
				Position.getBearingDifferenceDegrees(20, 365), precision);
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(20, 5),
				precision);
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(380, 5),
				precision);
		Assert.assertEquals(-25, Position.getBearingDifferenceDegrees(-20, 5),
				precision);
		Assert.assertEquals(5, Position.getBearingDifferenceDegrees(-20, -25),
				precision);
	}

	@Test
	public void testLatLonPresentation() {
		char d = 0x00B0;
		char m = '\'';
		Assert.assertEquals("25" + d + "30.00" + m + "S", Position
				.toDegreesMinutesDecimalMinutesLatitude(-25.5));
		Assert.assertEquals("0" + d + "00.00" + m + "N", Position
				.toDegreesMinutesDecimalMinutesLatitude(0));
		Assert.assertEquals("0" + d + "30.00" + m + "S", Position
				.toDegreesMinutesDecimalMinutesLatitude(-0.5));
		Assert.assertEquals("0" + d + "30.00" + m + "N", Position
				.toDegreesMinutesDecimalMinutesLatitude(0.5));
		Assert.assertEquals("1" + d + "30.00" + m + "N", Position
				.toDegreesMinutesDecimalMinutesLatitude(1.5));
		Assert.assertEquals("1" + d + "00.00" + m + "N", Position
				.toDegreesMinutesDecimalMinutesLatitude(1.0));

		Assert.assertEquals("1" + d + "00.00" + m + "E", Position
				.toDegreesMinutesDecimalMinutesLongitude(1.0));
		Assert.assertEquals("1" + d + "00.00" + m + "W", Position
				.toDegreesMinutesDecimalMinutesLongitude(-1.0));
	}

	@Test
	public void testGetPositionAlongPath() {

		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		{
			double distanceKm = p1.getDistanceToKm(p2);
			double bearingDegrees = p1.getBearingDegrees(p2);
			Position p3 = p1.predict(distanceKm * 0.7, bearingDegrees);

			// Expected position
			Position actual = p1.getPositionAlongPath(p2, 0.7);
			// Test expected Lat return position
			Assert.assertEquals(p3.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p3.getLon(), actual.getLon(), 0.01);
			// Test expected Lat return position
			Assert.assertEquals(35.47, actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(-109.11, actual.getLon(), 0.01);

		}

		{
			// If start point equals end point then a proportion
			// along the path should equal the start point.
			Position actual = p1.getPositionAlongPath(p1, 0.7);
			// Test expected Lat return position
			Assert.assertEquals(p1.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p1.getLon(), actual.getLon(), 0.01);
		}

		{
			// If proportion is 0.0 then should return start point
			Position actual = p1.getPositionAlongPath(p2, 0.0);
			// Test expected Lat return position
			Assert.assertEquals(p1.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p1.getLon(), actual.getLon(), 0.01);
		}

		{
			// If proportion is 1.0 then should return end point
			Position actual = p1.getPositionAlongPath(p2, 1.0);
			// Test expected Lat return position
			Assert.assertEquals(p2.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p2.getLon(), actual.getLon(), 0.01);
		}
	}

	@Test(expected = RuntimeException.class)
	public void testGetPositionAlongPathExceptionsGreater() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		p1.getPositionAlongPath(p2, 1.1);

	}

	@Test(expected = RuntimeException.class)
	public void testGetPositionAlongPathExceptionsLess() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		p1.getPositionAlongPath(p2, -0.1);
	}

	@Test(expected = NullPointerException.class)
	public void testGetPositionAlongPathExceptionsNull() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);

		p1.getPositionAlongPath(null, 0.7);

	}

	@Test
	public void testEnsureContinuous() {
		{
			Position a = new Position(-35, 179);
			Position b = new Position(-35, -178);
			Position c = b.ensureContinuous(a);
			Assert.assertEquals(182, c.getLon(), 0.0001);
			Assert.assertEquals(b.getLat(), c.getLat(), 0.0001);

			b = new Position(-35, 182);
			Assert.assertEquals(182, c.getLon(), 0.0001);
		}
		{
			Position a = new Position(-35, -2);
			Position b = new Position(-35, 360);
			Position c = b.ensureContinuous(a);
			Assert.assertEquals(0, c.getLon(), 0.0001);
		}

	}

}

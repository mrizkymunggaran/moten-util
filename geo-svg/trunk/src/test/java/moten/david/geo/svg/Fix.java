package moten.david.geo.svg;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Fix {
    private final String agent;
    private final String type;
    private final double lat;
    private final double lon;
    private final Date time;
    private final Map<String, String> identities;
    private final Map<String, String> properties;
    private final Double speedKnots;
    private final Double courseDegrees;

    public Fix(String agent, String type, double lat, double lon, Date time,
	    Map<String, String> identities, Map<String, String> properties,
	    Double speedKnots, Double courseDegrees) {
	super();
	this.agent = agent;
	this.type = type;
	this.lat = lat;
	this.lon = lon;
	this.time = time;
	this.identities = ImmutableMap.copyOf(identities);
	this.properties = ImmutableMap.copyOf(properties);
	this.speedKnots = speedKnots;
	this.courseDegrees = courseDegrees;
    }

    public String getAgent() {
	return agent;
    }

    public String getType() {
	return type;
    }

    public double getLat() {
	return lat;
    }

    public double getLon() {
	return lon;
    }

    public Date getTime() {
	return time;
    }

    public Map<String, String> getIdentities() {
	return identities;
    }

    public Map<String, String> getProperties() {
	return properties;
    }

    public Double getSpeedKnots() {
	return speedKnots;
    }

    public Double getCourseDegrees() {
	return courseDegrees;
    }

}

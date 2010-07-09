package moten.david.ets.client.model;

import java.util.Date;

public class Fix {
    String id;
    double lat;
    double lon;
    Date time;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Fix [id=" + id + ", lat=" + lat + ", lon=" + lon + ", time="
                + time + "]";
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

    public void setId(String id) {
        this.id = id;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

package org.example;

import java.util.List;

public class TripRequest {

    private List<GpsPoint> gpsPoints;

    public TripRequest() {
    }

    public List<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(List<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }
}

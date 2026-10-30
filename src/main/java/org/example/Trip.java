package org.example;

import java.util.List;

public class Trip {

    private String tripId;
    private List<GpsPoint> gpsPoints;
    private List<TripMilestone> milestones;

    public Trip(String tripId,
                List<GpsPoint> gpsPoints,
                List<TripMilestone> milestones) {

        this.tripId = tripId;
        this.gpsPoints = gpsPoints;
        this.milestones = milestones;
    }

    public String getTripId() {
        return tripId;
    }

    public List<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public List<TripMilestone> getMilestones() {
        return milestones;
    }
}
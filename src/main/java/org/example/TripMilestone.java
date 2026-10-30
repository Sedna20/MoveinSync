package org.example;

public class TripMilestone {

    private String type;
    private long timestamp;
    private double latitude;
    private double longitude;

    public TripMilestone(String type, long timestamp,
                         double latitude, double longitude) {

        this.type = type;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Check whether the actual GPS location is valid
    public boolean hasValidLocation() {

        return latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180
                && !(latitude == 0 && longitude == 0);
    }
}
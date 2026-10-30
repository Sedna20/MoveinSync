package org.example;

public class GpsPoint {
    private double latitude;
    private double longitude;
    private long timestamp;
    private double accuracy;

    public GpsPoint(double latitude, double longitude,
                    long timestamp, double accuracy) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public double getAccuracy() {
        return accuracy;
    }
}


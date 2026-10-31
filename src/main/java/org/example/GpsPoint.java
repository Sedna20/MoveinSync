package org.example;

public class GpsPoint {

    private double latitude;
    private double longitude;
    private long timestamp;
    private double accuracy;

    // Required for JSON conversion
    public GpsPoint() {
    }

    // Existing constructor
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}

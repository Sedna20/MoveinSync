package org.example;

public class ReconstructedPoint {

    private double latitude;
    private double longitude;
    private long timestamp;
    private String type;
    private String source;

    public ReconstructedPoint(
            double latitude,
            double longitude,
            long timestamp,
            String type,
            String source) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.type = type;
        this.source = source;
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

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public GpsPoint toGpsPoint() {
        return new GpsPoint(
                latitude,
                longitude,
                timestamp,
                10
        );
    }
}
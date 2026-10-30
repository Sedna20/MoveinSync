package org.example;

public class AnchorPoint {
    private String type;
    private long timestamp;
    private double latitude;
    private double longitude;
    private String source;

    public AnchorPoint(String type, long timestamp,
                       double latitude, double longitude,
                       String source) {

        this.type = type;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.source = source;
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

    public String getSource() {
        return source;
    }

}

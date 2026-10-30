package org.example;

public class DistanceSegment {
    private GpsPoint startPoint;
    private GpsPoint endPoint;
    private double distanceKm;
    private String rule;

    public DistanceSegment(
            GpsPoint startPoint,
            GpsPoint endPoint,
            double distanceKm,
            String rule) {

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distanceKm = distanceKm;
        this.rule = rule;
    }

    public GpsPoint getStartPoint() {
        return startPoint;
    }

    public GpsPoint getEndPoint() {
        return endPoint;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public String getRule() {
        return rule;
    }

}

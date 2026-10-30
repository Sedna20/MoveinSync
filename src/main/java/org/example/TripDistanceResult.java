package org.example;

import java.util.List;

public class TripDistanceResult {

    private double totalDistanceKm;
    private List<DistanceSegment> segments;
    private int routingCalls;

    public TripDistanceResult(
            double totalDistanceKm,
            List<DistanceSegment> segments,
            int routingCalls) {

        this.totalDistanceKm = totalDistanceKm;
        this.segments = segments;
        this.routingCalls = routingCalls;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public List<DistanceSegment> getSegments() {
        return segments;
    }

    public int getRoutingCalls() {
        return routingCalls;
    }
}
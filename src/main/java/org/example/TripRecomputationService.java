package org.example;

import java.util.List;

public class TripRecomputationService {

    private static final String RULE_VERSION = "1.0";

    private TripDistanceService tripDistanceService;

    public TripRecomputationService(
            TripDistanceService tripDistanceService) {

        this.tripDistanceService = tripDistanceService;
    }

    public TripDistanceResult recompute(
            List<GpsPoint> gpsPoints,
            List<AnchorPoint> anchors,
            RoutingCallTracker callTracker) {

        System.out.println(
                "Recomputing trip using rule version: "
                        + RULE_VERSION
        );

        return tripDistanceService.calculateTripDistance(
                gpsPoints,
                anchors,
                callTracker
        );
    }

    public String getRuleVersion() {
        return RULE_VERSION;
    }
}
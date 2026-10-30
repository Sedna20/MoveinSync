package org.example;

import java.util.ArrayList;
import java.util.List;

public class LatePingService {

    private TripRecomputationService recomputationService;

    public LatePingService(
            TripRecomputationService recomputationService) {

        this.recomputationService = recomputationService;
    }

    public BillingAdjustment processLatePing(
            double previouslyBilledKm,
            List<GpsPoint> updatedGpsPoints,
            List<AnchorPoint> anchors,
            RoutingCallTracker callTracker) {

        // Recalculate distance using updated GPS evidence
        TripDistanceResult updatedResult =
                recomputationService.recompute(
                        updatedGpsPoints,
                        anchors,
                        callTracker
                );

        // Create a billing adjustment
        return new BillingAdjustment(
                previouslyBilledKm,
                updatedResult.getTotalDistanceKm()
        );
    }
}
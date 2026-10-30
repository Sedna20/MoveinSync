package org.example;

import java.util.ArrayList;
import java.util.List;

public class TripDistanceService {

    private SegmentDistanceCalculator segmentCalculator;

    public TripDistanceService(
            SegmentDistanceCalculator segmentCalculator) {

        this.segmentCalculator = segmentCalculator;
    }

    public TripDistanceResult calculateTripDistance(
            List<GpsPoint> gpsPoints,
            List<AnchorPoint> anchors,
            RoutingCallTracker callTracker) {

        // Step 1: Combine GPS points and anchors
        List<ReconstructedPoint> points =
                TripReconstructor.reconstruct(
                        gpsPoints, anchors
                );

        List<DistanceSegment> segments =
                new ArrayList<>();

        double totalDistance = 0;

        // Step 2: Calculate every consecutive segment
        for (int i = 1; i < points.size(); i++) {

            GpsPoint start =
                    points.get(i - 1).toGpsPoint();

            GpsPoint end =
                    points.get(i).toGpsPoint();

            DistanceSegment segment =
                    segmentCalculator.calculateSegment(
                            start, end
                    );

            segments.add(segment);

            totalDistance += segment.getDistanceKm();
        }

        // Step 3: Return complete result
        return new TripDistanceResult(
                totalDistance,
                segments,
                callTracker.getTotalCalls()
        );
    }
}
package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TripReconstructor {

    public static List<ReconstructedPoint> reconstruct(
            List<GpsPoint> gpsPoints,
            List<AnchorPoint> anchors) {

        List<ReconstructedPoint> allPoints =
                new ArrayList<>();

        // Add GPS points
        for (GpsPoint point : gpsPoints) {

            ReconstructedPoint reconstructed =
                    new ReconstructedPoint(
                            point.getLatitude(),
                            point.getLongitude(),
                            point.getTimestamp(),
                            "GPS",
                            "GPS_EVIDENCE"
                    );

            allPoints.add(reconstructed);
        }

        // Add anchor points
        for (AnchorPoint anchor : anchors) {

            ReconstructedPoint reconstructed =
                    new ReconstructedPoint(
                            anchor.getLatitude(),
                            anchor.getLongitude(),
                            anchor.getTimestamp(),
                            anchor.getType(),
                            anchor.getSource()
                    );

            allPoints.add(reconstructed);
        }

        // Sort all points by timestamp
        allPoints.sort(
                Comparator.comparingLong(
                        ReconstructedPoint::getTimestamp
                )
        );

        return allPoints;
    }
}
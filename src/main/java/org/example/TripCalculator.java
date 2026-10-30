package org.example;
import java.util.List;

public class TripCalculator {
    public static double calculateTotalDistance(
            List<GpsPoint> points) {

        double totalDistance = 0;

        // Start from the second GPS point
        for (int i = 1; i < points.size(); i++) {

            GpsPoint previousPoint = points.get(i - 1);
            GpsPoint currentPoint = points.get(i);

            // Calculate distance between consecutive points
            double distance =
                    DistanceCalculator.calculateDistance(
                            previousPoint, currentPoint
                    );

            // Add distance to total
            totalDistance += distance;
        }

        return totalDistance;
    }
}
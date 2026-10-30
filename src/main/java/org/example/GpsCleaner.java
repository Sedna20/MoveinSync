package org.example;

import java.util.ArrayList;
import java.util.List;

public class GpsCleaner {

    // Remove duplicate GPS points
    public static List<GpsPoint> removeDuplicates(
            List<GpsPoint> points) {

        List<GpsPoint> cleanedPoints = new ArrayList<>();

        for (GpsPoint point : points) {

            boolean duplicate = false;

            for (GpsPoint existing : cleanedPoints) {

                if (point.getLatitude() == existing.getLatitude()
                        && point.getLongitude() == existing.getLongitude()
                        && point.getTimestamp() == existing.getTimestamp()) {

                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                cleanedPoints.add(point);
            }
        }

        return cleanedPoints;
    }

    // Sort GPS points by timestamp
    public static List<GpsPoint> sortByTimestamp(
            List<GpsPoint> points) {

        List<GpsPoint> sortedPoints = new ArrayList<>(points);

        sortedPoints.sort(
                (p1, p2) -> Long.compare(
                        p1.getTimestamp(),
                        p2.getTimestamp()
                )
        );
        return sortedPoints;
    }
        public static List<GpsPoint> removeLowAccuracy (
                List < GpsPoint > points,double maxAccuracy){

            List<GpsPoint> accuratePoints = new ArrayList<>();

            for (GpsPoint point : points) {

                if (point.getAccuracy() <= maxAccuracy) {
                    accuratePoints.add(point);
                }
            }

            return accuratePoints;

    }
    public static List<GpsPoint> removeTeleportPoints(
            List<GpsPoint> points, double maxSpeedKmph) {

        List<GpsPoint> validPoints = new ArrayList<>();

        if (points.isEmpty()) {
            return validPoints;
        }

        validPoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {

            GpsPoint previous =
                    validPoints.get(validPoints.size() - 1);

            GpsPoint current = points.get(i);

            double distance =
                    DistanceCalculator.calculateDistance(
                            previous, current
                    );

            double timeDifference =
                    (current.getTimestamp()
                            - previous.getTimestamp()) / 3600000.0;

            if (timeDifference <= 0) {
                continue;
            }

            double speed = distance / timeDifference;

            System.out.println("Speed: " + speed + " km/h");

            if (speed <= maxSpeedKmph) {
                validPoints.add(current);
            }
        }

        return validPoints;
    }
}

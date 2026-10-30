package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GpsCleanerTest {

    // TEST 1: Remove duplicate GPS points
    @Test
    void testRemoveDuplicates() {

        List<GpsPoint> points = new ArrayList<>();

        GpsPoint point1 = new GpsPoint(
                23.8315, 91.2868, 1000, 10
        );

        GpsPoint point2 = new GpsPoint(
                23.8315, 91.2868, 1000, 10
        );

        GpsPoint point3 = new GpsPoint(
                23.8320, 91.2870, 2000, 10
        );

        points.add(point1);
        points.add(point2);
        points.add(point3);

        List<GpsPoint> cleaned =
                GpsCleaner.removeDuplicates(points);

        assertEquals(2, cleaned.size());
    }


    // TEST 2: Remove low-accuracy GPS points
    @Test
    void testRemoveLowAccuracy() {

        List<GpsPoint> points = new ArrayList<>();

        points.add(new GpsPoint(
                23.8315, 91.2868, 1000, 10
        ));

        points.add(new GpsPoint(
                23.8320, 91.2870, 2000, 100
        ));

        points.add(new GpsPoint(
                23.8330, 91.2880, 3000, 20
        ));

        List<GpsPoint> accuratePoints =
                GpsCleaner.removeLowAccuracy(
                        points, 50
                );

        assertEquals(2, accuratePoints.size());

        for (GpsPoint point : accuratePoints) {
            assertTrue(point.getAccuracy() <= 50);
        }
    }


    // TEST 3: Remove teleport points
    @Test
    void testRemoveTeleportPoints() {

        List<GpsPoint> points = new ArrayList<>();

        // First valid point
        points.add(new GpsPoint(
                23.8315, 91.2868, 0, 10
        ));

        // Impossible jump after only 4 seconds
        points.add(new GpsPoint(
                23.9000, 91.3500, 4000, 10
        ));

        // Nearby valid point after 60 seconds
        points.add(new GpsPoint(
                23.8320, 91.2870, 60000, 10
        ));

        List<GpsPoint> cleaned =
                GpsCleaner.removeTeleportPoints(
                        points, 120
                );

        assertEquals(2, cleaned.size());

        assertEquals(
                23.8315,
                cleaned.get(0).getLatitude(),
                0.0001
        );

        assertEquals(
                23.8320,
                cleaned.get(1).getLatitude(),
                0.0001
        );
    }
}
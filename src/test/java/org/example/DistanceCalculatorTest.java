package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void testDistanceBetweenTwoPoints() {

        GpsPoint point1 = new GpsPoint(
                23.8315, 91.2868, 1000, 10
        );

        GpsPoint point2 = new GpsPoint(
                23.8320, 91.2870, 2000, 10
        );

        double distance =
                DistanceCalculator.calculateDistance(
                        point1, point2
                );

        assertTrue(distance > 0);

        assertTrue(distance < 1);
    }
}
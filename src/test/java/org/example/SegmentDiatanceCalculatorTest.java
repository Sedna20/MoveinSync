package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentDistanceCalculatorTest {

    private SegmentDistanceCalculator createCalculator() {

        return new SegmentDistanceCalculator(
                new MockRoutingProvider(),
                new RouteCache(),
                new RoutingCallTracker(),
                new CircuitBreaker()
        );
    }

    // TEST 1: Short GPS gap
    @Test
    void testShortGap() {

        SegmentDistanceCalculator calculator =
                createCalculator();

        GpsPoint start = new GpsPoint(
                23.8315, 91.2868, 10000, 10
        );

        GpsPoint end = new GpsPoint(
                23.8316, 91.2869, 20000, 10
        );

        DistanceSegment result =
                calculator.calculateSegment(start, end);

        assertEquals(
                "SHORT_GAP_AERIAL",
                result.getRule()
        );

        assertTrue(result.getDistanceKm() > 0);
    }

    // TEST 2: Parked cab
    @Test
    void testParkedCab() {

        SegmentDistanceCalculator calculator =
                createCalculator();

        GpsPoint start = new GpsPoint(
                23.8315, 91.2868, 100000, 10
        );

        GpsPoint end = new GpsPoint(
                23.83151, 91.28681, 300000, 10
        );

        DistanceSegment result =
                calculator.calculateSegment(start, end);

        assertEquals("PARKED", result.getRule());
    }

    // TEST 3: Routing provider
    @Test
    void testRouting() {

        SegmentDistanceCalculator calculator =
                createCalculator();

        GpsPoint start = new GpsPoint(
                23.8315, 91.2868, 1000000, 10
        );

        GpsPoint end = new GpsPoint(
                23.8500, 91.3000, 1900000, 10
        );

        DistanceSegment result =
                calculator.calculateSegment(start, end);

        assertEquals("ROUTED", result.getRule());

        assertTrue(result.getDistanceKm() > 0);
    }
}

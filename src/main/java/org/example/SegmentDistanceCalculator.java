
package org.example;

public class SegmentDistanceCalculator {

    private static final double MAX_SPEED_KMPH = 120.0;
    private static final double PARKED_SPEED_KMPH = 5.0;
    private static final long SHORT_GAP_SECONDS = 70;
    private static final double MAX_ROUTED_DISTANCE_KM = 100.0;

    private RoutingProvider routingProvider;
    private RouteCache routeCache;
    private RoutingCallTracker callTracker;
    private CircuitBreaker circuitBreaker;

    public SegmentDistanceCalculator(
            RoutingProvider routingProvider,
            RouteCache routeCache,
            RoutingCallTracker callTracker,
            CircuitBreaker circuitBreaker) {

        this.routingProvider = routingProvider;
        this.routeCache = routeCache;
        this.callTracker = callTracker;
        this.circuitBreaker = circuitBreaker;
    }

    public DistanceSegment calculateSegment(
            GpsPoint start,
            GpsPoint end) {

        // Calculate straight-line distance
        double straightDistance =
                DistanceCalculator.calculateDistance(start, end);

        // Calculate time difference in seconds
        long timeDifference =
                (end.getTimestamp() - start.getTimestamp()) / 1000;

        if (timeDifference <= 0) {
            return new DistanceSegment(
                    start, end, 0, "REJECTED_TIME"
            );
        }

        double hours = timeDifference / 3600.0;
        double speed = straightDistance / hours;

        // CASE 1: Short GPS gap
        if (timeDifference < SHORT_GAP_SECONDS) {

            if (speed > MAX_SPEED_KMPH) {
                return new DistanceSegment(
                        start, end, 0, "REJECTED_SPEED"
                );
            }

            return new DistanceSegment(
                    start, end,
                    straightDistance,
                    "SHORT_GAP_AERIAL"
            );
        }

        // CASE 2: Parked cab
        if (speed < PARKED_SPEED_KMPH) {

            return new DistanceSegment(
                    start, end,
                    straightDistance,
                    "PARKED"
            );
        }

        // CASE 3: Check route cache
        Double cachedDistance =
                routeCache.getRoute(start, end);

        if (cachedDistance != null) {

            return new DistanceSegment(
                    start, end,
                    cachedDistance,
                    "ROUTED_CACHED"
            );
        }

        // CASE 4: Check circuit breaker
        if (!circuitBreaker.allowRequest()) {

            return new DistanceSegment(
                    start, end,
                    straightDistance * 1.4,
                    "DEGRADED"
            );
        }

        // CASE 5: Call routing provider
        try {

            // Count actual provider requests
            callTracker.recordCall();

            double routedDistance =
                    routingProvider.getDrivingDistance(
                            start, end
                    );

            // Validate routing distance
            if (routedDistance <= 0
                    || routedDistance > MAX_ROUTED_DISTANCE_KM) {

                circuitBreaker.recordFailure();

                return new DistanceSegment(
                        start, end,
                        0,
                        "REJECTED_ROUTE"
                );
            }

            // Validate implied speed
            double routedSpeed =
                    routedDistance / hours;

            double estimatedSpeed =
                    straightDistance * 1.4 / hours;

            if (routedSpeed > MAX_SPEED_KMPH
                    && estimatedSpeed > MAX_SPEED_KMPH) {

                circuitBreaker.recordFailure();

                return new DistanceSegment(
                        start, end,
                        0,
                        "REJECTED_SPEED"
                );
            }

            // Provider succeeded
            circuitBreaker.recordSuccess();

            // Save route in cache
            routeCache.saveRoute(
                    start, end, routedDistance
            );

            return new DistanceSegment(
                    start, end,
                    routedDistance,
                    "ROUTED"
            );

        } catch (Exception e) {

            // Provider failed
            circuitBreaker.recordFailure();

            // Use fallback distance
            return new DistanceSegment(
                    start, end,
                    straightDistance * 1.4,
                    "DEGRADED"
            );
        }
    }
}

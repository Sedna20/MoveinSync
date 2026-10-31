package org.example;

import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/trips")
public class FailureDemoController {

    private final RoutingCallTracker callTracker =
            new RoutingCallTracker();

    private final CircuitBreaker circuitBreaker =
            new CircuitBreaker();

    private final TripDistanceService tripDistanceService =
            new TripDistanceService(
                    new SegmentDistanceCalculator(
                            new FailingRoutingProvider(),
                            new RouteCache(),
                            callTracker,
                            circuitBreaker
                    )
            );

    @PostMapping("/failure-demo")
    public TripDistanceResult simulateFailure(
            @RequestBody TripRequest request) {

        if (request == null ||
                request.getGpsPoints() == null ||
                request.getGpsPoints().size() < 2) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "At least two GPS points are required."
            );
        }

        callTracker.reset();

        return tripDistanceService.calculateTripDistance(
                request.getGpsPoints(),
                Collections.emptyList(),
                callTracker
        );
    }
}
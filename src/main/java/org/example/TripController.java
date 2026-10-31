
package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final RoutingCallTracker callTracker =
            new RoutingCallTracker();

    private final TripDistanceService tripDistanceService =
            new TripDistanceService(
                    new SegmentDistanceCalculator(
                            new MockRoutingProvider(),
                            new RouteCache(),
                            callTracker,
                            new CircuitBreaker()
                    )
            );

    @PostMapping("/calculate")
    public TripDistanceResult calculate(
            @RequestBody TripRequest request) {

        if (request == null ||
                request.getGpsPoints() == null ||
                request.getGpsPoints().size() < 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
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
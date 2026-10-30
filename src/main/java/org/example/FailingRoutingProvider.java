package org.example;

public class FailingRoutingProvider implements RoutingProvider {

    @Override
    public double getDrivingDistance(
            GpsPoint start,
            GpsPoint end) {

        throw new RuntimeException(
                "Routing provider is unavailable!"
        );
    }
}

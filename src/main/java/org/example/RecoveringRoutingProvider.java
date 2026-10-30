package org.example;

public class RecoveringRoutingProvider implements RoutingProvider {

    @Override
    public double getDrivingDistance(
            GpsPoint start,
            GpsPoint end) {

        double straightDistance =
                DistanceCalculator.calculateDistance(
                        start, end
                );

        return straightDistance * 1.4;
    }
}
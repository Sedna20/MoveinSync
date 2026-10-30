package org.example;

public interface RoutingProvider {

    double getDrivingDistance(
            GpsPoint start,
            GpsPoint end
    );
}

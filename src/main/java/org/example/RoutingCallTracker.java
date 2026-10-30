package org.example;

public class RoutingCallTracker {
    private int totalCalls = 0;

    public synchronized void recordCall() {
        totalCalls++;
    }

    public synchronized int getTotalCalls() {
        return totalCalls;
    }

    public synchronized void reset() {
        totalCalls = 0;
    }
}

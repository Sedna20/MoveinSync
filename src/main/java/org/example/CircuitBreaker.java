package org.example;

public class CircuitBreaker {

    private enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private State state = State.CLOSED;

    private int failureCount = 0;

    private static final int FAILURE_THRESHOLD = 3;

    private static final long WAIT_TIME = 10000;

    private long openedAt = 0;

    private boolean testRequestInProgress = false;

    // Check whether a request is allowed
    public synchronized boolean allowRequest() {

        if (state == State.CLOSED) {
            return true;
        }

        if (state == State.OPEN) {

            long currentTime = System.currentTimeMillis();

            if (currentTime - openedAt >= WAIT_TIME) {

                state = State.HALF_OPEN;
                testRequestInProgress = true;

                return true;
            }

            return false;
        }

        // Only one test request in HALF_OPEN
        if (state == State.HALF_OPEN
                && !testRequestInProgress) {

            testRequestInProgress = true;
            return true;
        }

        return false;
    }

    // Call when provider succeeds
    public synchronized void recordSuccess() {

        failureCount = 0;
        state = State.CLOSED;
        testRequestInProgress = false;
    }

    // Call when provider fails
    public synchronized void recordFailure() {

        failureCount++;

        if (state == State.HALF_OPEN
                || failureCount >= FAILURE_THRESHOLD) {

            state = State.OPEN;
            openedAt = System.currentTimeMillis();
        }

        testRequestInProgress = false;
    }

    // Get current state
    public synchronized String getState() {
        return state.toString();
    }
}

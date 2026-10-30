package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {

    // TEST 1: Circuit breaker starts CLOSED
    @Test
    void testInitialState() {

        CircuitBreaker breaker = new CircuitBreaker();

        assertEquals("CLOSED", breaker.getState());

        assertTrue(breaker.allowRequest());
    }

    // TEST 2: Circuit opens after three failures
    @Test
    void testCircuitOpensAfterFailures() {

        CircuitBreaker breaker = new CircuitBreaker();

        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();

        assertEquals("OPEN", breaker.getState());

        assertFalse(breaker.allowRequest());
    }

    // TEST 3: Successful request closes circuit
    @Test
    void testSuccessfulRequestClosesCircuit() {

        CircuitBreaker breaker = new CircuitBreaker();

        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();

        assertEquals("OPEN", breaker.getState());

        // Simulate successful recovery
        breaker.recordSuccess();

        assertEquals("CLOSED", breaker.getState());
        assertTrue(breaker.allowRequest());
    }
}
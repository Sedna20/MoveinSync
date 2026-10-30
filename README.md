MoveInSync – GPS Gap Backfill & Billable Distance Reconstruction

1. Project Overview

MoveInSync is a Java-based prototype designed to reconstruct reliable and explainable billable trip distances from GPS data that may be incomplete, duplicated, inaccurate, or out of order.

The system combines cleaned GPS evidence with trip milestones and anchor points to estimate distance more reliably. Each distance segment is assigned a rule label so that the calculation can be understood and audited.

2. Problem Statement

GPS signals can become unreliable due to tunnels, basements, network interruptions, or device errors. Simply adding the distances between available GPS points may underestimate or overestimate the actual trip distance.

This project addresses the problem by:

- Cleaning unreliable GPS records.
- Using employee pickup and drop milestones as anchor points.
- Reconstructing a chronological trip path.
- Applying different distance calculation rules.
- Handling routing-provider failures.
- Supporting recalculation when late GPS data arrives.

3. Technologies Used

- Java
- Maven
- JUnit 5
- IntelliJ IDEA
- Haversine formula for geographical distance calculation

4. Architecture

The prototype follows a modular, object-oriented structure.

Main components

Component| Responsibility
GpsCleaner| Removes duplicate, inaccurate and teleport GPS points
DistanceCalculator| Calculates straight-line distance using Haversine formula
AnchorPointBuilder| Creates anchors from milestones and fallback locations
TripReconstructor| Combines GPS points and anchors chronologically
SegmentDistanceCalculator| Applies distance rules to individual segments
RoutingProvider| Defines the routing-provider interface
RouteCache| Stores previously calculated route distances
CircuitBreaker| Handles repeated routing-provider failures
TripDistanceService| Calculates total trip distance
TripRecomputationService| Recalculates trip distance
LatePingService| Processes late GPS data
BillingAdjustmentSigner| Signs and verifies billing adjustments

5. Implemented Features

GPS Data Cleaning

- Duplicate GPS point removal.
- Timestamp-based sorting.
- Low-accuracy point filtering.
- Teleport detection using maximum speed threshold.

Anchor Point Reconstruction

- Actual pickup and drop locations.
- Ad-hoc employee fallback to profile home location.
- Planned employee fallback to planned stop location.
- Optional no-show anchor handling.
- Office anchor validation using a 400-metre threshold.

Distance Calculation Rules

Rule| Description
SHORT_GAP_AERIAL| Straight-line distance for short GPS gaps
PARKED| Straight-line distance when the cab is moving very slowly
ROUTED| Distance returned by the routing provider
ROUTED_CACHED| Previously cached route distance
DEGRADED| Fallback estimate when routing is unavailable
REJECTED_SPEED| Rejects implausible movement
REJECTED_TIME| Rejects invalid time intervals

Reliability and Billing

- Route caching with bounded capacity and expiry.
- Routing-call tracking.
- Circuit breaker for provider failures.
- Automatic fallback distance estimation.
- Late-ping recalculation.
- Billing adjustment calculation.
- HMAC-SHA256 signature generation and verification.

6. Running the Project

Requirements

- Java JDK
- Maven
- IntelliJ IDEA (or another Java IDE)

Run the application

Open the project in IntelliJ and run the "Main.java" file.

Run automated tests

Run the test classes under:

"src/test/java/org/example"

Alternatively, run all tests using Maven:

mvn test

7. Testing

The project currently contains 13 automated JUnit tests covering:

- Haversine distance calculation.
- GPS duplicate removal.
- GPS accuracy filtering.
- Teleport detection.
- Short-gap distance calculation.
- Parked-cab distance calculation.
- Routing-based distance calculation.
- Circuit-breaker state handling.
- Billing adjustment calculation.
- Billing signature validation.
- Tamper detection.

All 13 tests passed in the current development environment.

8. Current Limitations

This is a prototype implementation. The following production-level capa
MoveInSync – System Design Document

1. System Overview

MoveInSync is a Java-based prototype for reconstructing billable trip distances from incomplete or unreliable GPS data.

The system combines cleaned GPS records with employee milestones and fallback anchor points. It calculates the distance between consecutive reconstructed points using rule-based logic and records the rule applied to every segment.

The main design goal is accuracy, reliability, explainability and traceability.

2. Architecture

The prototype follows a modular, object-oriented architecture.

Architecture Diagram

flowchart TD
A[GPS Pings] --> B[GPS Cleaner]
C[Trip Milestones] --> D[Anchor Point Builder]
E[Employee and Planned Stop Data] --> D
B --> F[Trip Reconstructor]
D --> F
F --> G[Segment Distance Calculator]
G --> H{Distance Rule}
H -->|Short Gap / Parked| I[Haversine Distance]
H -->|Long Gap| J[Routing Provider]
J --> K[Route Cache]
J --> L[Circuit Breaker]
I --> M[Distance Segments]
K --> M
L --> M
M --> N[Trip Distance Service]
N --> O[Trip Distance Result]
O --> P[Recomputation and Billing Adjustment]

Component Responsibilities

Component| Responsibility
GpsCleaner| Cleans and validates GPS evidence
AnchorPointBuilder| Creates actual and fallback anchors
TripReconstructor| Merges GPS points and anchors by timestamp
SegmentDistanceCalculator| Selects the appropriate distance rule
RoutingProvider| Provides driving-distance estimates through an interface
RouteCache| Reuses previously calculated route distances
CircuitBreaker| Controls requests during routing failures
TripDistanceService| Calculates total trip distance
TripRecomputationService| Recalculates trip distance
LatePingService| Calculates billing changes from late GPS data
BillingAdjustmentSigner| Signs and verifies billing adjustments

3. Main Workflow

1. Receive GPS pings and trip milestones.
2. Remove duplicate GPS records.
3. Sort GPS records by timestamp.
4. Filter low-accuracy GPS points.
5. Reject impossible teleport movements using a configurable speed threshold.
6. Build anchors from actual employee milestones.
7. Use profile home, planned stop or planned office locations when applicable.
8. Merge GPS points and anchors into chronological order.
9. Calculate the distance for each consecutive segment.
10. Apply a suitable distance rule and label each segment.
11. Add segment distances to calculate the total trip distance.
12. Recompute the trip when late GPS evidence arrives and calculate a billing adjustment.

4. Distance Calculation Strategy

The system applies inexpensive calculations before requesting driving-route estimates.

Condition| Applied Rule
Gap below 70 seconds with plausible speed| SHORT_GAP_AERIAL
Cab moving below 5 km/h| PARKED
Longer gap requiring road distance| ROUTED
Previously calculated route available| ROUTED_CACHED
Routing provider unavailable| DEGRADED
Implausible movement| REJECTED_SPEED
Invalid time interval| REJECTED_TIME

The Haversine formula calculates the straight-line distance between geographical coordinates.

The routing provider is currently mocked and estimates driving distance using a multiplier on straight-line distance. A real road-routing integration is a future enhancement.

5. Important Workflow: Handling a Routing Failure

sequenceDiagram
participant S as Segment Calculator
participant C as Route Cache
participant B as Circuit Breaker
participant R as Routing Provider

    S->>C: Check cached route
    C-->>S: Cache miss
    S->>B: Check whether request is allowed
    B-->>S: Request allowed
    S->>R: Request driving distance
    R-->>S: Provider failure
    S->>B: Record failure
    S->>S: Estimate straight-line distance × 1.4
    S-->>S: Mark segment DEGRADED

After repeated failures, the circuit breaker opens and prevents further requests temporarily. Once the waiting period has passed, it allows a trial request to check provider recovery.

## 6. Failure Mode and Handling

| Failure scenario | Prototype handling |
| --- | --- |
| Duplicate GPS pings | Duplicate records are removed |
| GPS points arrive out of order | Points are sorted by timestamp |
| Poor GPS accuracy | Low-accuracy records are filtered |
| Impossible GPS jump | Teleport point is rejected |
| Missing GPS evidence | Milestone anchors help reconstruct the path |
| Missing actual employee location | Eligible fallback location is used |
| Routing provider failure | Circuit breaker and degraded estimate are used |
| Repeated route request | Cached distance is reused |
| Late GPS ping | Trip is recalculated and billing delta is generated |
| Modified billing adjustment | Signature verification detects tampering |

## 7. Recalculation and Billing Integrity

The prototype supports recalculation when new GPS evidence arrives after an earlier distance calculation.

The `LatePingService` compares the previously billed distance with the recalculated distance.

The difference is represented as a billing adjustment:

- Positive delta: additional distance.
- Negative delta: distance reduction.
- Zero delta: no change.

The `BillingAdjustmentSigner` uses HMAC-SHA256 to generate and verify a signature for the adjustment.

The current implementation uses a demonstration secret. Production deployment requires secure secret management and persistent billing history.

## 8. Complexity Considerations

Let:

- n = number of GPS points.
- a = number of anchor points.

| Operation | Current complexity |
| --- | --- |
| Duplicate removal | O(n²) |
| Timestamp sorting | O(n log n) |
| Accuracy filtering | O(n) |
| Teleport detection | O(n) after sorting |
| Trip reconstruction sorting | O((n+a) log(n+a)) |
| Segment calculation | O(n+a), excluding routing-provider latency |
| Cache lookup | O(1) average for the current hash-based key map |

The duplicate-removal implementation can be optimised using a hash-based data structure for larger GPS datasets.

## 9. Testing Strategy

The current project includes 13 automated JUnit tests covering:

- Haversine distance calculation.
- GPS duplicate removal.
- Accuracy filtering.
- Teleport detection.
- Short-gap distance calculation.
- Parked-cab distance calculation.
- Routing-based distance calculation.
- Circuit-breaker state handling.
- Billing adjustment calculation.
- Signature verification.
- Tampering detection.

All 13 tests passed in the current development environment.

## 10. Limitations and Future Improvements

The current implementation is a prototype. Future improvements include:

- Real road-routing provider integration.
- Database persistence for trips and billing records.
- REST API development.
- Authentication and role-based access control.
- Tenant isolation.
- Persistent, versioned recomputation.
- Concurrency and failure-injection testing.
- Performance benchmarking with throughput and p50/p99 latency.
- Secure environment-based secret management.

## 11. Conclusion

The design demonstrates a modular approach to reconstructing billable trip distances from imperfect GPS evidence. By combining GPS cleaning, milestone-based anchors, rule-based segment calculation, caching, fault handling and billing adjustments, the prototype provides an explainable foundation for a more reliable trip-distance system.
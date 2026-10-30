
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =====================================
        // STEP 1: CREATE GPS POINTS
        // =====================================

        List<GpsPoint> gpsPoints = new ArrayList<>();

        gpsPoints.add(new GpsPoint(23.8315, 91.2868, 0, 10));
        gpsPoints.add(new GpsPoint(23.8320, 91.2870, 60000, 10));
        gpsPoints.add(new GpsPoint(23.8320, 91.2870, 60000, 10));
        gpsPoints.add(new GpsPoint(23.8400, 91.2900, 120000, 10));
        gpsPoints.add(new GpsPoint(23.8330, 91.2880, 180000, 10));
        gpsPoints.add(new GpsPoint(23.8331, 91.2881, 181000, 10));

        System.out.println("Original GPS Points: " + gpsPoints.size());


        // =====================================
        // STEP 2: CLEAN GPS DATA
        // =====================================

        List<GpsPoint> cleaned =
                GpsCleaner.removeDuplicates(gpsPoints);

        cleaned = GpsCleaner.sortByTimestamp(cleaned);
        cleaned = GpsCleaner.removeLowAccuracy(cleaned, 50);
        cleaned = GpsCleaner.removeTeleportPoints(cleaned, 120);

        System.out.println("Cleaned GPS Points: " + cleaned.size());


        // =====================================
        // STEP 3: CREATE TRIP MILESTONES
        // =====================================

        List<TripMilestone> milestones = new ArrayList<>();

        milestones.add(new TripMilestone(
                "PICKUP", 1000, 23.8315, 91.2868));

        milestones.add(new TripMilestone(
                "DROP", 2000, 23.8330, 91.2880));

        milestones.add(new TripMilestone(
                "TRIP_END", 3000, 23.8340, 91.2890));


        // =====================================
        // STEP 4: BUILD ACTUAL ANCHORS
        // =====================================

        List<AnchorPoint> anchors =
                AnchorPointBuilder.buildAnchors(milestones);

        System.out.println("\nActual Anchors:");

        for (AnchorPoint anchor : anchors) {
            System.out.println(
                    anchor.getType() + " - " + anchor.getSource());
        }


        // =====================================
        // STEP 5: AD-HOC EMPLOYEE FALLBACK
        // =====================================

        Employee adHocEmployee = new Employee(
                "EMP001", "AD_HOC", 23.8310, 91.2860);

        TripMilestone invalidPickup = new TripMilestone(
                "PICKUP", 4000, 0, 0);

        AnchorPoint adHocAnchor =
                AnchorPointBuilder.buildEmployeeAnchor(
                        invalidPickup, adHocEmployee, null);

        System.out.println("\nAd-hoc Anchor:");

        if (adHocAnchor != null) {
            System.out.println(adHocAnchor.getSource());
        }


        // =====================================
        // STEP 6: PLANNED EMPLOYEE FALLBACK
        // =====================================

        Employee plannedEmployee = new Employee(
                "EMP002", "PLANNED", 0, 0);

        PlannedStop plannedStop = new PlannedStop(
                "EMP002", 5000, 23.8350, 91.2900);

        AnchorPoint plannedAnchor =
                AnchorPointBuilder.buildEmployeeAnchor(
                        invalidPickup, plannedEmployee, plannedStop);

        System.out.println("\nPlanned Employee Anchor:");

        if (plannedAnchor != null) {
            System.out.println(plannedAnchor.getSource());
        }


        // =====================================
        // STEP 7: NO-SHOW ANCHOR
        // =====================================

        TripMilestone noShow = new TripMilestone(
                "NO_SHOW", 6000, 0, 0);

        AnchorPoint noShowAnchor =
                AnchorPointBuilder.buildNoShowAnchor(
                        noShow, plannedStop, true);

        System.out.println("\nNo-show Anchor:");

        if (noShowAnchor != null) {
            System.out.println(noShowAnchor.getSource());
        }

        AnchorPoint disabledNoShow =
                AnchorPointBuilder.buildNoShowAnchor(
                        noShow, plannedStop, false);

        System.out.println("No-show disabled: "
                + (disabledNoShow == null));


        // =====================================
        // STEP 8: OFFICE ANCHOR
        // =====================================

        TripMilestone tripEnd = new TripMilestone(
                "TRIP_END", 7000, 23.8340, 91.2890);

        AnchorPoint officeAnchor =
                AnchorPointBuilder.buildOfficeAnchor(
                        tripEnd, 23.8341, 91.2891);

        System.out.println("\nOffice Anchor:");

        if (officeAnchor != null) {
            System.out.println(officeAnchor.getSource());
        }


        // =====================================
        // STEP 9: INITIALIZE ROUTING COMPONENTS
        // =====================================

        RoutingProvider routingProvider =
                new MockRoutingProvider();

        RouteCache routeCache = new RouteCache();

        RoutingCallTracker callTracker =
                new RoutingCallTracker();

        CircuitBreaker circuitBreaker =
                new CircuitBreaker();

        SegmentDistanceCalculator calculator =
                new SegmentDistanceCalculator(
                        routingProvider,
                        routeCache,
                        callTracker,
                        circuitBreaker);


        // =====================================
        // STEP 10: SHORT GAP TEST
        // =====================================

        GpsPoint point1 = new GpsPoint(
                23.8315, 91.2868, 10000, 10);

        GpsPoint point2 = new GpsPoint(
                23.8316, 91.2869, 20000, 10);

        DistanceSegment shortSegment =
                calculator.calculateSegment(point1, point2);

        System.out.println("\nShort Gap Test:");

        System.out.println("Rule: " + shortSegment.getRule());
        System.out.println("Distance: "
                + shortSegment.getDistanceKm() + " km");


        // =====================================
        // STEP 11: PARKED CAB TEST
        // =====================================

        GpsPoint parkedPoint1 = new GpsPoint(
                23.8315, 91.2868, 100000, 10);

        GpsPoint parkedPoint2 = new GpsPoint(
                23.83151, 91.28681, 300000, 10);

        DistanceSegment parkedSegment =
                calculator.calculateSegment(
                        parkedPoint1, parkedPoint2);

        System.out.println("\nParked Cab Test:");

        System.out.println("Rule: " + parkedSegment.getRule());
        System.out.println("Distance: "
                + parkedSegment.getDistanceKm() + " km");


        // =====================================
        // STEP 12: ROUTING AND CACHE TEST
        // =====================================

        GpsPoint routePoint1 = new GpsPoint(
                23.8315, 91.2868, 1000000, 10);

        GpsPoint routePoint2 = new GpsPoint(
                23.8500, 91.3000, 1900000, 10);

        DistanceSegment firstRoute =
                calculator.calculateSegment(
                        routePoint1, routePoint2);

        DistanceSegment secondRoute =
                calculator.calculateSegment(
                        routePoint1, routePoint2);

        System.out.println("\nRouting and Cache Test:");

        System.out.println("First route: "
                + firstRoute.getRule());

        System.out.println("Second route: "
                + secondRoute.getRule());

        System.out.println("First distance: "
                + firstRoute.getDistanceKm() + " km");

        System.out.println("Second distance: "
                + secondRoute.getDistanceKm() + " km");

        System.out.println("Cached routes: "
                + routeCache.getCacheSize());

        System.out.println("Routing provider calls: "
                + callTracker.getTotalCalls());


        // =====================================
        // STEP 13: CIRCUIT BREAKER STATUS
        // =====================================

        System.out.println("\nCircuit Breaker Test:");

        System.out.println("Circuit Breaker State: "
                + circuitBreaker.getState());
        System.out.println("\nProvider Failure Test:");

        RoutingProvider failingProvider =
                new FailingRoutingProvider();

        RouteCache failureCache = new RouteCache();

        RoutingCallTracker failureTracker =
                new RoutingCallTracker();

        CircuitBreaker failureBreaker =
                new CircuitBreaker();

        SegmentDistanceCalculator failureCalculator =
                new SegmentDistanceCalculator(
                        failingProvider,
                        failureCache,
                        failureTracker,
                        failureBreaker);

// Create points that require routing
        GpsPoint failurePoint1 = new GpsPoint(
                23.8315, 91.2868, 1000000, 10);

        GpsPoint failurePoint2 = new GpsPoint(
                23.8500, 91.3000, 1900000, 10);

// Attempt 1
        DistanceSegment failure1 =
                failureCalculator.calculateSegment(
                        failurePoint1, failurePoint2);

        System.out.println("Attempt 1: " + failure1.getRule());

// Attempt 2
        DistanceSegment failure2 =
                failureCalculator.calculateSegment(
                        failurePoint1, failurePoint2);

        System.out.println("Attempt 2: " + failure2.getRule());

// Attempt 3
        DistanceSegment failure3 =
                failureCalculator.calculateSegment(
                        failurePoint1, failurePoint2);

        System.out.println("Attempt 3: " + failure3.getRule());

// Display circuit breaker state
        System.out.println("Circuit Breaker State: "
                + failureBreaker.getState());

        System.out.println("Provider Calls: "
                + failureTracker.getTotalCalls());
        DistanceSegment blockedRequest =
                failureCalculator.calculateSegment(
                        failurePoint1, failurePoint2);

        System.out.println("\nBlocked Request Test:");

        System.out.println("Rule: "
                + blockedRequest.getRule());

        System.out.println("Provider Calls After Blocking: "
                + failureTracker.getTotalCalls());

        System.out.println("Circuit Breaker State: "
                + failureBreaker.getState());
        System.out.println("\nProvider Recovery Test:");

// Wait for the circuit breaker timeout (10 seconds)
        System.out.println("Waiting for circuit breaker timeout...");

        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Recovery wait interrupted.");
        }

// Create a working routing provider
        RoutingProvider recoveringProvider =
                new RecoveringRoutingProvider();

// Use the SAME circuit breaker that was previously OPEN
        SegmentDistanceCalculator recoveryCalculator =
                new SegmentDistanceCalculator(
                        recoveringProvider,
                        new RouteCache(),
                        new RoutingCallTracker(),
                        failureBreaker);

// Make a recovery request
        DistanceSegment recoveryResult =
                recoveryCalculator.calculateSegment(
                        failurePoint1, failurePoint2);

        System.out.println("Recovery Rule: "
                + recoveryResult.getRule());

        System.out.println("Circuit Breaker State After Recovery: "
                + failureBreaker.getState());
        // STEP 17: COMPLETE TRIP DISTANCE TEST

        System.out.println("\n===== COMPLETE TRIP TEST =====");

        // Create GPS evidence
        List<GpsPoint> tripGps = new ArrayList<>();

        tripGps.add(new GpsPoint(
                23.8315, 91.2868, 100000, 10));

        tripGps.add(new GpsPoint(
                23.8320, 91.2870, 160000, 10));

        tripGps.add(new GpsPoint(
                23.8350, 91.2900, 600000, 10));

        tripGps.add(new GpsPoint(
                23.8400, 91.2950, 1200000, 10));

        // Create trip anchors
        List<AnchorPoint> tripAnchors = new ArrayList<>();

        tripAnchors.add(new AnchorPoint(
                "PICKUP", 300000,
                23.8330, 91.2880, "ACTUAL"));

        tripAnchors.add(new AnchorPoint(
                "DROP", 900000,
                23.8380, 91.2930, "ACTUAL"));

        // Create routing components
        RoutingCallTracker tripTracker =
                new RoutingCallTracker();

        SegmentDistanceCalculator tripSegmentCalculator =
                new SegmentDistanceCalculator(
                        new MockRoutingProvider(),
                        new RouteCache(),
                        tripTracker,
                        new CircuitBreaker()
                );

        // Calculate complete trip distance
        TripDistanceService tripService =
                new TripDistanceService(
                        tripSegmentCalculator
                );

        TripDistanceResult tripResult =
                tripService.calculateTripDistance(
                        tripGps,
                        tripAnchors,
                        tripTracker
                );

        // Display every segment
        for (DistanceSegment segment :
                tripResult.getSegments()) {

            System.out.println(
                    segment.getRule()
                            + " : "
                            + segment.getDistanceKm()
                            + " km"
            );
        }
        System.out.println("Total Billable Distance: "
                + tripResult.getTotalDistanceKm() + " km");

        System.out.println("Routing Calls: "
                + tripResult.getRoutingCalls());
        // STEP 18: IDEMPOTENT RECOMPUTATION TEST

        System.out.println("\n===== RECOMPUTATION TEST =====");

        TripRecomputationService recomputationService =
                new TripRecomputationService(tripService);

        TripDistanceResult firstCalculation =
                recomputationService.recompute(
                        tripGps,
                        tripAnchors,
                        tripTracker
                );

        TripDistanceResult secondCalculation =
                recomputationService.recompute(
                        tripGps,
                        tripAnchors,
                        tripTracker
                );

        System.out.println("First Calculation: "
                + firstCalculation.getTotalDistanceKm());

        System.out.println("Second Calculation: "
                + secondCalculation.getTotalDistanceKm());

        System.out.println("Same Result: "
                + (Double.compare(
                firstCalculation.getTotalDistanceKm(),
                secondCalculation.getTotalDistanceKm()
        ) == 0));

        System.out.println("Rule Version: "
                + recomputationService.getRuleVersion());
        // STEP 19: LATE GPS PING TEST

        System.out.println("\n===== LATE GPS PING TEST =====");

        LatePingService latePingService =
                new LatePingService(recomputationService);

        // Simulate a GPS ping arriving after billing
        List<GpsPoint> updatedGpsPoints =
                new ArrayList<>(tripGps);

        updatedGpsPoints.add(
                new GpsPoint(
                        23.8360, 91.2910, 750000, 10
                )
        );

        // Previously billed distance
        double previouslyBilledKm =
                tripResult.getTotalDistanceKm();

        BillingAdjustment adjustment =
                latePingService.processLatePing(
                        previouslyBilledKm,
                        updatedGpsPoints,
                        tripAnchors,
                        tripTracker
                );

        System.out.println("Previously Billed: "
                + adjustment.getPreviouslyBilledKm() + " km");

        System.out.println("Recalculated Distance: "
                + adjustment.getRecalculatedKm() + " km");

        System.out.println("Billing Adjustment: "
                + adjustment.getDeltaKm() + " km");

        System.out.println("Adjustment Type: "
                + adjustment.getAdjustmentType());
        // STEP 20: BILLING SIGNATURE TEST

        System.out.println("\n===== BILLING SIGNATURE TEST =====");

        // Generate signature
        String signature =
                BillingAdjustmentSigner.sign(adjustment);

        System.out.println("Generated Signature: "
                + signature);

        // Verify original adjustment
        boolean validSignature =
                BillingAdjustmentSigner.verify(
                        adjustment, signature
                );

        System.out.println("Original Adjustment Valid: "
                + validSignature);

        // Create a modified adjustment to simulate tampering
        BillingAdjustment modifiedAdjustment =
                new BillingAdjustment(
                        adjustment.getPreviouslyBilledKm(),
                        adjustment.getRecalculatedKm() + 1.0
                );

        // Verify modified adjustment using original signature
        boolean tamperedSignature =
                BillingAdjustmentSigner.verify(
                        modifiedAdjustment, signature
                );

        System.out.println("Modified Adjustment Valid: "
                + tamperedSignature);

    }
}

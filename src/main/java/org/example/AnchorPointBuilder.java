package org.example;

import java.util.ArrayList;
import java.util.List;

public class AnchorPointBuilder {

    // STEP 1: Build anchors from actual trip milestones
    public static List<AnchorPoint> buildAnchors(
            List<TripMilestone> milestones) {

        List<AnchorPoint> anchors = new ArrayList<>();

        for (TripMilestone milestone : milestones) {

            if (milestone.hasValidLocation()) {

                AnchorPoint anchor = new AnchorPoint(
                        milestone.getType(),
                        milestone.getTimestamp(),
                        milestone.getLatitude(),
                        milestone.getLongitude(),
                        "ACTUAL"
                );

                anchors.add(anchor);
            }
        }

        return anchors;
    }


    // STEP 2: Build employee anchor with fallback locations
    public static AnchorPoint buildEmployeeAnchor(
            TripMilestone milestone,
            Employee employee,
            PlannedStop plannedStop) {

        if (milestone == null) {
            return null;
        }

        // Case 1: Actual milestone location is valid
        if (milestone.hasValidLocation()) {

            return new AnchorPoint(
                    milestone.getType(),
                    milestone.getTimestamp(),
                    milestone.getLatitude(),
                    milestone.getLongitude(),
                    "ACTUAL"
            );
        }

        // Case 2: Ad-hoc rider fallback to profile home
        if (employee != null
                && employee.getRiderType() != null
                && employee.getRiderType().equalsIgnoreCase("AD_HOC")) {

            double latitude = employee.getHomeLatitude();
            double longitude = employee.getHomeLongitude();

            if (isValidLocation(latitude, longitude)) {

                return new AnchorPoint(
                        milestone.getType(),
                        milestone.getTimestamp(),
                        latitude,
                        longitude,
                        "PROFILE_HOME"
                );
            }
        }

        // Case 3: Planned rider fallback to planned stop
        if (employee != null
                && employee.getRiderType() != null
                && employee.getRiderType().equalsIgnoreCase("PLANNED")
                && plannedStop != null) {

            if (isValidLocation(
                    plannedStop.getLatitude(),
                    plannedStop.getLongitude())) {

                return new AnchorPoint(
                        milestone.getType(),
                        plannedStop.getPlannedTime(),
                        plannedStop.getLatitude(),
                        plannedStop.getLongitude(),
                        "PLANNED_LOCATION"
                );
            }
        }

        return null;
    }


    // STEP 3: Build no-show anchor
    public static AnchorPoint buildNoShowAnchor(
            TripMilestone milestone,
            PlannedStop plannedStop,
            boolean includeNoShows) {

        if (!includeNoShows) {
            return null;
        }

        if (milestone == null
                || milestone.getType() == null
                || !milestone.getType().equalsIgnoreCase("NO_SHOW")) {

            return null;
        }

        // Case 1: Use actual no-show location
        if (milestone.hasValidLocation()) {

            return new AnchorPoint(
                    "NO_SHOW",
                    milestone.getTimestamp(),
                    milestone.getLatitude(),
                    milestone.getLongitude(),
                    "ACTUAL"
            );
        }

        // Case 2: Use planned location as fallback
        if (plannedStop != null
                && isValidLocation(
                plannedStop.getLatitude(),
                plannedStop.getLongitude())) {

            return new AnchorPoint(
                    "NO_SHOW",
                    milestone.getTimestamp(),
                    plannedStop.getLatitude(),
                    plannedStop.getLongitude(),
                    "PLANNED_LOCATION"
            );
        }

        return null;
    }


    // STEP 4: Build office anchor
    public static AnchorPoint buildOfficeAnchor(
            TripMilestone tripEnd,
            double plannedOfficeLatitude,
            double plannedOfficeLongitude) {

        if (tripEnd == null) {
            return null;
        }

        if (!isValidLocation(
                plannedOfficeLatitude,
                plannedOfficeLongitude)) {
            return null;
        }

        // Use actual trip-end location if within 400 metres
        if (tripEnd.hasValidLocation()) {

            GpsPoint actualPoint = new GpsPoint(
                    tripEnd.getLatitude(),
                    tripEnd.getLongitude(),
                    tripEnd.getTimestamp(),
                    10
            );

            GpsPoint officePoint = new GpsPoint(
                    plannedOfficeLatitude,
                    plannedOfficeLongitude,
                    tripEnd.getTimestamp(),
                    10
            );

            double distance =
                    DistanceCalculator.calculateDistance(
                            actualPoint, officePoint
                    );

            if (distance <= 0.4) {

                return new AnchorPoint(
                        "OFFICE",
                        tripEnd.getTimestamp(),
                        tripEnd.getLatitude(),
                        tripEnd.getLongitude(),
                        "ACTUAL"
                );
            }
        }

        // Otherwise use planned office location
        return new AnchorPoint(
                "OFFICE",
                tripEnd.getTimestamp(),
                plannedOfficeLatitude,
                plannedOfficeLongitude,
                "PLANNED_OFFICE"
        );
    }


    // STEP 5: Validate coordinates
    private static boolean isValidLocation(
            double latitude, double longitude) {

        return latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180
                && !(latitude == 0 && longitude == 0);
    }
}
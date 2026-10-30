package org.example;

public class PlannedStop {

    private String employeeId;
    private long plannedTime;
    private double latitude;
    private double longitude;

    public PlannedStop(String employeeId, long plannedTime,
                       double latitude, double longitude) {

        this.employeeId = employeeId;
        this.plannedTime = plannedTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public long getPlannedTime() {
        return plannedTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
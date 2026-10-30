package org.example;

public class Employee {
    private String employeeId;
    private String riderType;
    private double homeLatitude;
    private double homeLongitude;

    public Employee(String employeeId, String riderType,
                    double homeLatitude, double homeLongitude) {

        this.employeeId = employeeId;
        this.riderType = riderType;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getRiderType() {
        return riderType;
    }

    public double getHomeLatitude() {
        return homeLatitude;
    }

    public double getHomeLongitude() {
        return homeLongitude;
    }
}

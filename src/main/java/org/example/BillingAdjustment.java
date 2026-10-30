package org.example;

public class BillingAdjustment {

    private double previouslyBilledKm;
    private double recalculatedKm;
    private double deltaKm;

    public BillingAdjustment(
            double previouslyBilledKm,
            double recalculatedKm) {

        this.previouslyBilledKm = previouslyBilledKm;
        this.recalculatedKm = recalculatedKm;

        this.deltaKm =
                recalculatedKm - previouslyBilledKm;
    }

    public double getPreviouslyBilledKm() {
        return previouslyBilledKm;
    }

    public double getRecalculatedKm() {
        return recalculatedKm;
    }

    public double getDeltaKm() {
        return deltaKm;
    }

    public String getAdjustmentType() {

        if (deltaKm > 0) {
            return "ADDITIONAL_DISTANCE";
        }

        if (deltaKm < 0) {
            return "DISTANCE_REDUCTION";
        }

        return "NO_CHANGE";
    }
}
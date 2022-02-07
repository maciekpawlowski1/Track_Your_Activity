package com.pawlowski.trackyouractivity.models;

public class TrackingStopUpdate {
    boolean isTracking;

    public TrackingStopUpdate(boolean isTracking) {
        this.isTracking = isTracking;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void setTracking(boolean tracking) {
        isTracking = tracking;
    }
}

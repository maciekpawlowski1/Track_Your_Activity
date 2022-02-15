package com.pawlowski.trackyouractivity.models;

import android.location.Location;

public class LocationUpdateModel {
    private Location mLocation;
    private float mAllDistance;
    private double mCurrentSpeed;
    private double mAllKcal;

    public LocationUpdateModel(Location location, float allDistance, double currentSpeed, double allKcal) {
        this.mLocation = location;
        this.mAllDistance = allDistance;
        this.mCurrentSpeed = currentSpeed;
        mAllKcal = allKcal;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    public float getAllDistance() {
        return mAllDistance;
    }

    public void setAllDistance(float allDistance) {
        this.mAllDistance = allDistance;
    }

    public double getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.mCurrentSpeed = currentSpeed;
    }

    public double getAllKcal() {
        return mAllKcal;
    }

    public void setAllKcal(double mAllKcal) {
        this.mAllKcal = mAllKcal;
    }
}

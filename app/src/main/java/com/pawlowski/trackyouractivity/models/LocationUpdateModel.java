package com.pawlowski.trackyouractivity.models;

import android.location.Location;

public class LocationUpdateModel {
    private Location location;
    private float allDistance;
    private double currentSpeed;

    public LocationUpdateModel(Location location, float allDistance, double currentSpeed) {
        this.location = location;
        this.allDistance = allDistance;
        this.currentSpeed = currentSpeed;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getAllDistance() {
        return allDistance;
    }

    public void setAllDistance(float allDistance) {
        this.allDistance = allDistance;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }
}

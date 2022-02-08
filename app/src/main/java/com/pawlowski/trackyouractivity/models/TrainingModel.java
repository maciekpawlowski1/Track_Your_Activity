package com.pawlowski.trackyouractivity.models;

public class TrainingModel {
    private int id;
    private double distance;
    private long time;
    private int kcal;
    private boolean isFinished;
    private long date;

    public TrainingModel(long date, double distance, long time, int kcal, boolean isFinished) {
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.kcal = kcal;
        this.isFinished = isFinished;
    }

    public TrainingModel(double distance, long time, int kcal, boolean isFinished) {
        this.date = -1;
        this.distance = distance;
        this.time = time;
        this.kcal = kcal;
        this.isFinished = isFinished;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

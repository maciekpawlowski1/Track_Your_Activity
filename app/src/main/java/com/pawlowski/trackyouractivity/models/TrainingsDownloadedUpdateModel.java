package com.pawlowski.trackyouractivity.models;

public class TrainingsDownloadedUpdateModel {
    private boolean newTrainings;

    public TrainingsDownloadedUpdateModel(boolean newTrainings) {
        this.newTrainings = newTrainings;
    }

    public boolean isNewTrainings() {
        return newTrainings;
    }

    public void setNewTrainings(boolean newTrainings) {
        this.newTrainings = newTrainings;
    }
}

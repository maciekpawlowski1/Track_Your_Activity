package com.pawlowski.trackyouractivity.models;

public class UserModel {
    private String key;
    private String name;
    private String dateOfBirth;
    private int weight;
    private int goal;

    public UserModel(String key, String name, String dateOfBirth, int weight, int goal) {
        this.key = key;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.goal = goal;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}

package com.pawlowski.trackyouractivity.database;

import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    SharedPreferences mSharedPreferences;

    public SharedPreferencesHelper(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }


    public void setCurrentKcal(float kcal)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat("kcal", kcal);
        editor.commit();
    }

    public double getCurrentKcal()
    {
        return mSharedPreferences.getFloat("kcal", 0);
    }

    public void setWeight(int weight)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("weight", weight);
        editor.apply();
    }

    public int getWeight()
    {
        return mSharedPreferences.getInt("weight", 0);
    }


    public void setLastUpdatesCheckTime(long time)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("lastUpdatesCheck", time);
        editor.apply();
    }

    public long getLastUpdatesCheckTime()
    {
        return mSharedPreferences.getLong("lastUpdatesCheck", 0);
    }

    public boolean isTrackingActive()
    {
        return mSharedPreferences.getBoolean("isTracking", false);
    }

    public float getCurrentDistance()
    {
        return mSharedPreferences.getFloat("distance", 0);
    }


    public boolean isProfileSaved()
    {
        return mSharedPreferences.getBoolean("is_profile_saved", false);
    }

    public void setProfileSaved(boolean isSaved)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("is_profile_saved", isSaved);
        editor.commit();
    }

    public void setTrackingActive(boolean isTurnedOn)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isTracking", isTurnedOn);
        editor.commit();
    }

    public int getWeeklyGoal()
    {
        return mSharedPreferences.getInt("weekly_goal", 0);
    }

    public void setWeeklyGoal(int goal)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("weekly_goal", goal);
        editor.commit();
    }

    public void setDistance(float currentDistance)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat("distance", currentDistance);
        editor.commit();
    }

    public void setDistanceInBackground(float currentDistance)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat("distance", currentDistance);
        editor.apply();
    }

    public long getCurrentTime()
    {
        return mSharedPreferences.getLong("time", 0);
    }

    public void setCurrentTime(long time)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("time", time);
        editor.commit();
    }

    public void setCurrentTimeInBackground(long time)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("time", time);
        editor.apply();
    }

    public void resetCurrentTraining()
    {
        setTrackingActive(false);
        setDistance(0);
        setCurrentTime(0);
        setCurrentKcal(0);
    }
}

package com.pawlowski.trackyouractivity.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    SharedPreferences mSharedPreferences;

    public SharedPreferencesHelper(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public boolean isTrackingActive()
    {
        return mSharedPreferences.getBoolean("isTracking", false);
    }

    public float getCurrentDistance()
    {
        return mSharedPreferences.getFloat("distance", 0);
    }

    public void setTrackingActive(boolean isTurnedOn)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isTracking", isTurnedOn);
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
    }
}

package com.pawlowski.trackyouractivity.service;

import android.util.Log;

import com.pawlowski.trackyouractivity.base.BaseObservable;
import com.pawlowski.trackyouractivity.consts.Const;

public class SpeedChecker extends BaseObservable<SpeedChecker.SpeedChangeListener> {
    private double mPreviousDistance = -1;
    private double mCurrentSpeed = 0;
    private long mPreviousCheckTime = -1;

    void updateDistance(double currentDistance, long lastDistanceCheckTime)
    {
        if(mPreviousCheckTime == -1)
        {
            mPreviousCheckTime = lastDistanceCheckTime;
            mPreviousDistance = currentDistance;
        }
        else if(lastDistanceCheckTime - mPreviousCheckTime > 1000 * Const.SPEED_TIME_CHECKING_DELTA_IN_SECONDS)
        {
            mCurrentSpeed = ((currentDistance-mPreviousDistance)/((lastDistanceCheckTime-mPreviousCheckTime)/1000.))/(1000/3600.);
            mPreviousCheckTime = lastDistanceCheckTime;
            mPreviousDistance = currentDistance;
            notifyListeners();
        }

    }

    @Override
    protected void notifyListeners() {
        for(SpeedChangeListener l:listeners)
        {
            l.onSpeedChange(mCurrentSpeed);
        }
    }

    interface SpeedChangeListener
    {
        void onSpeedChange(double currentSpeed);
    }
}

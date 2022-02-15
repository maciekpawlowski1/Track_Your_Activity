package com.pawlowski.trackyouractivity.service;

import com.pawlowski.trackyouractivity.base.BaseObservable;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;

import static com.pawlowski.trackyouractivity.models.TrainingModel.TrainingType.CYCLING;
import static com.pawlowski.trackyouractivity.models.TrainingModel.TrainingType.NORDIC_WALKING;
import static com.pawlowski.trackyouractivity.models.TrainingModel.TrainingType.ROLLER_SKATING;
import static com.pawlowski.trackyouractivity.models.TrainingModel.TrainingType.RUNNING;

public class SpeedAndKcalChecker extends BaseObservable<SpeedAndKcalChecker.SpeedChangeListener> {
    private double mPreviousDistance = -1;
    private double mCurrentSpeed = 0;
    private long mPreviousCheckTime = -1;
    private double mAllKcal;
    private final int mTrainingType;
    private final int mWeight;

    public SpeedAndKcalChecker(double kcal, int trainingType, int weight) {
        this.mAllKcal = kcal;
        mTrainingType = trainingType;
        mWeight = weight;
    }

    void updateDistance(double currentDistance, long lastDistanceCheckTime)
    {
        if(mPreviousCheckTime == -1)
        {
            mPreviousCheckTime = lastDistanceCheckTime;
            mPreviousDistance = currentDistance;
        }
        else if(lastDistanceCheckTime - mPreviousCheckTime > 1000 * ConstAndStaticMethods.SPEED_TIME_CHECKING_DELTA_IN_SECONDS)
        {
            mCurrentSpeed = ((currentDistance-mPreviousDistance)/((lastDistanceCheckTime-mPreviousCheckTime)/1000.))/(1000/3600.);
            double met = 0;
            if(mTrainingType == RUNNING.ordinal())
            {
                met = ConstAndStaticMethods.getRunningMet(mCurrentSpeed);
            }
            else if(mTrainingType == CYCLING.ordinal())
            {
                met = ConstAndStaticMethods.getCyclingMet(mCurrentSpeed);
            }
            else if(mTrainingType == NORDIC_WALKING.ordinal())
            {
                met = ConstAndStaticMethods.getWalkingMet(mCurrentSpeed);
            }
            else if(mTrainingType == ROLLER_SKATING.ordinal())
            {
                met = ConstAndStaticMethods.getRollerSkatingMet(mCurrentSpeed);
            }
            mAllKcal += ConstAndStaticMethods.getKcalForGivenSecondsHavingMet(mWeight, met, (lastDistanceCheckTime-mPreviousCheckTime)/1000);
            mPreviousCheckTime = lastDistanceCheckTime;
            mPreviousDistance = currentDistance;

            notifyListeners();
        }

    }

    @Override
    protected void notifyListeners() {
        for(SpeedChangeListener l:listeners)
        {
            l.onSpeedChange(mCurrentSpeed, mAllKcal);
        }
    }

    interface SpeedChangeListener
    {
        void onSpeedChange(double currentSpeed, double allKcal);
    }
}

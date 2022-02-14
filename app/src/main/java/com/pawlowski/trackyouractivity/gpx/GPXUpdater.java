package com.pawlowski.trackyouractivity.gpx;

import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.urizev.gpx.beans.Waypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.WorkerThread;

public class GPXUpdater {
    private String mTrainingKey;
    private final GPXUseCase mGpxUseCase;
    private final ArrayList<Waypoint> mWaypoints = new ArrayList<>();
    private int mLastUpdatedSize;
    private final AtomicBoolean isFinished;
    private final Thread workingThread;

    public GPXUpdater(String trainingKey, GPXUseCase gpxUseCase) {
        mTrainingKey = trainingKey;
        mGpxUseCase = gpxUseCase;
        isFinished = new AtomicBoolean(false);
        isFinished.set(false);
        workingThread = new Thread(this::work);

    }

    @WorkerThread
    private void work()
    {
        while (!isFinished.get())
        {
            synchronized (mWaypoints)
            {
                if(mWaypoints.size() != mLastUpdatedSize)
                {
                    mGpxUseCase.writeToGpx(mWaypoints, mTrainingKey+".gpx");
                    mLastUpdatedSize = mWaypoints.size();
                }
            }

            try {
                Thread.sleep(ConstAndStaticMethods.GPX_UPDATE_TIME_DELTA_IN_SECONDS * 1000L);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                return;
            }
        }
    }

    public void startUpdating(List<Waypoint> previousWaypoints)
    {
        if(!isFinished.get())
        {
            mWaypoints.clear();
            mWaypoints.addAll(previousWaypoints);
            workingThread.start();
        }
    }

    public void stopUpdating()
    {
        isFinished.set(true);
        workingThread.interrupt();
    }

    public void addWaypoint(Waypoint waypoint)
    {
        new Thread(() -> {
            synchronized (mWaypoints)
            {
                mWaypoints.add(waypoint);
            }
        }).start();

    }


}

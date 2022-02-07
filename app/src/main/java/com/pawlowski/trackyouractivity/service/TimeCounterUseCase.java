package com.pawlowski.trackyouractivity.service;

import android.os.Handler;
import android.os.Looper;

import com.pawlowski.trackyouractivity.base.BaseObservable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimeCounterUseCase extends BaseObservable<TimeCounterUseCase.OnTimeUpdateListener> {
    AtomicLong mTime;
    volatile long mLastCheck;
    private final Thread mThread;
    private final Handler mHandler;
    private final AtomicBoolean mIsFinished;

    public TimeCounterUseCase(long time, long lastCheck) {
        this.mTime = new AtomicLong(time);
        this.mLastCheck = lastCheck;
        mIsFinished = new AtomicBoolean(false);
        mHandler = new Handler(Looper.getMainLooper());
        mThread = new Thread(() -> {
            while (!mIsFinished.get())
            {
                long newTime = System.currentTimeMillis();
                TimeCounterUseCase.this.mTime.addAndGet(newTime-TimeCounterUseCase.this.mLastCheck);
                TimeCounterUseCase.this.mLastCheck = newTime;
                mHandler.post(this::notifyListeners);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        });
        mThread.start();
    }

    public void stopCounting()
    {
        mIsFinished.set(false);
    }

    @Override
    protected void notifyListeners() {
        for(OnTimeUpdateListener l:listeners)
        {
            l.onTimeUpdate(mTime.get());
        }
    }

    interface OnTimeUpdateListener
    {
        void onTimeUpdate(long currentSeconds);
    }
}

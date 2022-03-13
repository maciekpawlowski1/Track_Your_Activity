package com.pawlowski.trackyouractivity.composition;

import android.content.Context;

import com.pawlowski.trackyouractivity.MyApplication;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

abstract public class BaseWorker extends ListenableWorker {
    private AppCompositionRoot appCompositionRoot;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public BaseWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    protected AppCompositionRoot getAppCompositionRoot() {
        if(appCompositionRoot == null)
            appCompositionRoot = ((MyApplication)getApplicationContext()).getAppCompositionRoot();
        return appCompositionRoot;
    }
}

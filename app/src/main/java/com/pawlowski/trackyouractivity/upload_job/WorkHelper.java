package com.pawlowski.trackyouractivity.upload_job;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class WorkHelper {

    static final String TAG = "TrackYourActivityUploading";

    public LiveData<WorkInfo> startWorkIfNotExists(Context context, String accountKey)
    {
        Log.d("WorkHelper", "Trying to start");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setInputData(
                                new Data.Builder()
                                    .putString("account_key", accountKey)
                                .build()
                        )
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setConstraints(constraints)
                        .build();

       WorkManager.getInstance(context).enqueueUniqueWork(TAG, ExistingWorkPolicy.APPEND_OR_REPLACE, uploadWorkRequest);//.enqueue(uploadWorkRequest);//

       return WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWorkRequest.getId());
    }
}

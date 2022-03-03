package com.pawlowski.trackyouractivity.download_job;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class DownloadWorkHelper {
    static final String TAG = "TrackYourActivityDownloading";

    public LiveData<WorkInfo> startWorkIfNotExists(Context context, String accountKey, List<String> keysToDownload)
    {
        Log.d("WorkHelper", "Trying to start");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        String[] keysArray = new String[keysToDownload.size()];
        for(int i=0;i< keysToDownload.size();i++)
            keysArray[i] = keysToDownload.get(i);

        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(DownloadWorker.class)
                        .setInputData(
                                new Data.Builder()
                                        .putString("account_key", accountKey)
                                        .putStringArray("keysToDownload", keysArray)
                                        .build()
                        )
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueueUniqueWork(TAG, ExistingWorkPolicy.APPEND_OR_REPLACE, uploadWorkRequest);//.enqueue(uploadWorkRequest);//

        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWorkRequest.getId());
    }
}

package com.pawlowski.trackyouractivity.download_job;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public class DownloadWorker extends ListenableWorker {
    private static final String NOTIFICATION_CHANNEL_ID = "TrackYourActivityDownloadWorkerChannelId";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "TrackYourActivityDownloadWorkerChannelName";
    private final DBHandler mDBHandler;
    private final FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private final File mFilesDir;


    public DownloadWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d("worker", "constructor");
        mDBHandler = new DBHandler(context);
        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper();
        mFilesDir = context.getFilesDir();

    }

    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }
        int flag;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            flag = PendingIntent.FLAG_IMMUTABLE;
        }
        else
        {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), SignInActivity.class), flag))
                .setSmallIcon(R.drawable.upload_icon)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setLocalOnly(true)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentText("Downloading trainings")
                .build();
        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<ForegroundInfo> completer) throws Exception {
                completer.set(new ForegroundInfo(532, notification));
                return "Foreground info (downloading)";
            }
        });
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        Log.d("worker", "startWork");

        String accountKey = getInputData().getString("account_key");
        List<String> keysToDownload = new ArrayList<String>(Arrays.asList(Objects.requireNonNull(getInputData().getStringArray("keysToDownload"))));



        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<Result> completer) throws Exception {
                if(accountKey == null)
                    completer.set(Result.failure());

                work(accountKey, keysToDownload, completer);
                Log.d("worker", "attaching");
                return "Training downloading process";
            }
        });

    }


    private void work(String accountKey, List<String> keysToDownload, @NonNull CallbackToFutureAdapter.Completer<Result> completer)
    {
        List<String> alreadyHadKeys = mDBHandler.getAllTrainingsKeys(accountKey);
        List<Task<Void>> tasks = new ArrayList<>();
        for(String key:keysToDownload)
        {
            if(!alreadyHadKeys.contains(key)) //Just to make sure, to reduce work
            {
                tasks.add(downloadTraining(accountKey, key));
            }

        }

        for(String key:alreadyHadKeys)
        {
            if(!new File(mFilesDir.getAbsolutePath() + File.separator, key+".gpx").exists())
            {
                tasks.add(downloadOnlyGpx(accountKey, key));
            }
        }

        Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                completer.set(Result.success()); //What if there is no image in Cloud Storage? If would be try, then infinity retrying?
            }
        });
    }

    private Task<Void> downloadOnlyGpx(String accountKey, String trainingKey)
    {
        File file = new File(mFilesDir.getAbsolutePath() + File.separator, trainingKey+".gpx");
        FileDownloadTask gpxTask = mFirebaseDatabaseHelper.downloadGpxFile(accountKey, trainingKey, file);
        return Tasks.whenAll(gpxTask);
    }

    private Task<Void> downloadTraining(String accountKey, String trainingKey)
    {
        Task<TrainingModel> trainingTask = mFirebaseDatabaseHelper.downloadTraining(accountKey, trainingKey);
        File file = new File(mFilesDir.getAbsolutePath() + File.separator, trainingKey+".gpx");
        FileDownloadTask gpxTask = null;
        if(!file.exists())
        {
            gpxTask = mFirebaseDatabaseHelper.downloadGpxFile(accountKey, trainingKey, file);
        }




        Task<Boolean> trainingAllTask = trainingTask.continueWith(new Continuation<TrainingModel, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<TrainingModel> task) throws Exception {
                if(task.isSuccessful())
                {
                    mDBHandler.insertTrainingAfterDownloading(task.getResult(), accountKey);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        if(gpxTask != null)
        {
            return Tasks.whenAll(gpxTask, trainingAllTask);
        }
        else
        {
            return Tasks.whenAll(trainingAllTask);
        }
    }
}

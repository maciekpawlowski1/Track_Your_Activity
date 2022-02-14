package com.pawlowski.trackyouractivity.upload_job;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.UploadTask;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public class UploadWorker extends ListenableWorker {

    private static final String NOTIFICATION_CHANNEL_ID = "TrackYourActivityWorkerChannelId";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "TrackYourActivityWorkerChannelName";
    DBHandler mDBHandler;
    FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    File mFilesDir;

    public UploadWorker(Context context, WorkerParameters workerParams) {
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
                .setContentText("Uploading training")
                .build();
        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<ForegroundInfo> completer) throws Exception {
                completer.set(new ForegroundInfo(531, notification));
                return "Foreground info";
            }
        });
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        Log.d("worker", "startWork");

        String accountKey = getInputData().getString("account_key");



        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<Result> completer) throws Exception {
                if(accountKey == null)
                    completer.set(Result.failure());

                work(accountKey, completer);
                Log.d("worker", "attaching");
                return "Gpx uploading process";
            }
        });

    }


    private void work(String accountKey, @NonNull CallbackToFutureAdapter.Completer<Result> completer)
    {
        List<TrainingModel> trainings = mDBHandler.getAllNotSavedTrainings();
        Log.d("worker", "to save: " + trainings.size());
        List<UploadTask> tasks = new ArrayList<>();
        List<Integer> taskTrainingIds = new ArrayList<>();
        for(TrainingModel training: trainings)
        {
            File file = new File(mFilesDir.getAbsolutePath() + File.separator, training.getKey()+".gpx");
            if(file.exists())
            {
                Log.d("worker", "Running some task");
                tasks.add(mFirebaseDatabaseHelper.uploadFile(accountKey, training.getKey(), file));
                taskTrainingIds.add(training.getId());
            }
            else
            {
                Log.d("worker", "Some training doesn't have gpx file");
            }
        }

        Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.d("worker", "success, saved");

                    for(Integer id:taskTrainingIds)
                    {
                        mDBHandler.updateTrainingAsSaved(id);
                        Log.d("worker", "one task successful");
                    }
                    completer.set(Result.success());
                }
                else
                {
                    Log.d("worker", "failure");
                    for(int i=0;i<tasks.size();i++)
                    {
                        if(tasks.get(i).isSuccessful())
                        {
                            Log.d("worker", "one task successful");
                            mDBHandler.updateTrainingAsSaved(taskTrainingIds.get(i));
                        }
                    }
                    completer.set(Result.retry());
                }
            }
        });

    }
}
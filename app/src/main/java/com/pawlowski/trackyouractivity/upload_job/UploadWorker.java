package com.pawlowski.trackyouractivity.upload_job;

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
    private final DBHandler mDBHandler;
    private final FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private final File mFilesDir;


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
        List<TrainingModel> trainings = mDBHandler.getAllNotSavedTrainings(accountKey);
        List<UploadTask> gpxTasks = new ArrayList<>();
        List<Integer> taskTrainingIds = new ArrayList<>();
        for(TrainingModel training: trainings)
        {
            File file = new File(mFilesDir.getAbsolutePath() + File.separator, training.getKey()+".gpx");
            if(file.exists())
            {
                Log.d("worker", "Running some task");
                gpxTasks.add(mFirebaseDatabaseHelper.uploadFile(accountKey, training.getKey(), file));
                taskTrainingIds.add(training.getId());
            }
            else
            {
                Log.d("worker", "Some training doesn't have gpx file");
            }
        }


        List<TrainingModel> trainingsRemoteDatabase = mDBHandler.getAllNotSavedTrainingsInRealtimeDatabase(accountKey);
        List<Task<Void>> tasksRemoteDatabase = new ArrayList<>();

        for(TrainingModel training: trainingsRemoteDatabase)
        {
            tasksRemoteDatabase.add(mFirebaseDatabaseHelper.addTraining(accountKey, training.getKey(), training.getDistance(), training.getKcal(), training.getTime(), training.getDate(), training.getTrainingType()));
        }


        Task<Void> remoteTasksContinuation = Tasks.whenAll(tasksRemoteDatabase).continueWith(new Continuation<Void, Void>() {

            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful())
                {
                    for(TrainingModel training: trainingsRemoteDatabase)
                    {
                        mDBHandler.updateTrainingAsSavedInRealtimeDatabase(training.getId());
                    }
                    return null;
                }
                else
                {
                    for(int i = 0;i<tasksRemoteDatabase.size();i++)
                    {
                        if(tasksRemoteDatabase.get(i).isSuccessful())
                        {
                            mDBHandler.updateTrainingAsSavedInRealtimeDatabase(trainingsRemoteDatabase.get(i).getId());
                        }
                    }
                    throw new Exception("Some trainings didn't save");
                }


            }
        });
        Task<Void> gpxContinuationTask = Tasks.whenAll(gpxTasks).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful())
                {
                    Log.d("worker", "success, saved");

                    for(Integer id:taskTrainingIds)
                    {
                        mDBHandler.updateTrainingAsSaved(id);
                        Log.d("worker", "one task successful");
                    }
                   return null;
                }
                else
                {
                    Log.d("worker", "failure");
                    for(int i=0;i<gpxTasks.size();i++)
                    {
                        if(gpxTasks.get(i).isSuccessful())
                        {
                            Log.d("worker", "one task successful");
                            mDBHandler.updateTrainingAsSaved(taskTrainingIds.get(i));
                        }
                    }
                    throw new Exception("Some trainings gpx didn't save");
                }
            }
        });


        Tasks.whenAll(remoteTasksContinuation, gpxContinuationTask)
                .addOnCompleteListener(new OnCompleteListener<>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.d("UploadWorker", "All trainings uploaded");
                    completer.set(Result.success());
                }
                else
                {
                    Log.d("UploadWorker", "Some trainings not uploaded. Retrying...");

                    completer.set(Result.retry());
                }
            }
        });

    }
}
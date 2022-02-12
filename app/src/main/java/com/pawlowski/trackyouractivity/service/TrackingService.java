package com.pawlowski.trackyouractivity.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.gpx.GPXUpdater;
import com.pawlowski.trackyouractivity.gpx.GPXUseCase;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.models.TrackingStopUpdate;
import com.urizev.gpx.beans.Waypoint;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class TrackingService extends Service implements TimeCounterUseCase.OnTimeUpdateListener, SpeedChecker.SpeedChangeListener {
    public TrackingService() {
    }

    LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;
    DBHandler mDbHandler;
    Location mLastLocation = null;
    float mAllDistance = 0;
    double mCurrentSpeed = 0;
    long mStartingSeconds = 0;
    long mCurrentSeconds = 0;
    int mTrainingId;

    ArrayList<Waypoint> mWaypoints = new ArrayList<>();


    TimeCounterUseCase mTimeCounterUseCase;
    SpeedChecker mSpeedChecker;
    SharedPreferencesHelper mSharedPreferencesHelper;

    TextToSpeech mTextToSpeech;

    GPXUpdater mGPXUpdater = null;

    GPXUseCase mGPXUseCase;

    @Override
    public void onCreate() {
        super.onCreate();


        mStartingSeconds = System.currentTimeMillis();
        mSpeedChecker = new SpeedChecker();
        mSpeedChecker.registerListener(this);
        mSharedPreferencesHelper = new SharedPreferencesHelper(getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, MODE_MULTI_PROCESS));
        mDbHandler = new DBHandler(getApplicationContext());
        mTrainingId = mDbHandler.getCurrentTrainingId();
        mGPXUseCase = new GPXUseCase(getFilesDir());
        mGPXUpdater = new GPXUpdater(mTrainingId, mGPXUseCase);
        buildNotification();
        initLocationCallback();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startTrackingLocation();


        mGPXUseCase.readFromGpxTask(mTrainingId+".gpx").addOnSuccessListener(new OnSuccessListener<List<Waypoint>>() {
            @Override
            public void onSuccess(List<Waypoint> waypoints) {
                waypoints.addAll(mWaypoints);
                mWaypoints.clear();
                mWaypoints.addAll(waypoints);
                mGPXUpdater.startUpdating(waypoints);
            }
        });










        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    mTextToSpeech.setLanguage(Locale.ENGLISH);
                    vibrate();
                    mTextToSpeech.speak("Tracking started!",TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });


    }



    private void vibrate()
    {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(300);
        }
    }





    private void buildNotification() {
        String stop = ConstAndStaticMethods.INTENT_FILTER_STOP_TEXT;
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    ConstAndStaticMethods.NOTIFICATION_CHANNEL_ID,
                    ConstAndStaticMethods.SERVICE_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    this, ConstAndStaticMethods.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.run_icon)
                    .setContentTitle("Tracking")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .addAction(new NotificationCompat.Action(R.drawable.pause_icon, "Pause", broadcastIntent))
                    //.setContentIntent(broadcastIntent)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setChannelId(ConstAndStaticMethods.NOTIFICATION_CHANNEL_ID)
                    .build();

            startForeground(ConstAndStaticMethods.NOTIFICATION_ID, notification);
        }
        else
        {
            // Create the persistent notification
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Tracking")
                    .addAction(new Notification.Action(R.drawable.pause_icon, "Pause", broadcastIntent))

                    //Make this notification ongoing so it can’t be dismissed by the user//
                    .setOngoing(true)
                    //.setContentIntent(broadcastIntent)
                    .setSmallIcon(R.drawable.run_icon);
            startForeground(ConstAndStaticMethods.NOTIFICATION_ID, builder.build());
        }


    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopReceiver = null;
            stopTrackingLocation();
            stopSelf();

        }
    };




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(stopReceiver != null)
            unregisterReceiver(stopReceiver);

    }

    private void initLocationCallback() {
        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult)
            {
                Location location = locationResult.getLastLocation();

                if (mLastLocation != null) {
                    mAllDistance += mLastLocation.distanceTo(location);
                }
                mSpeedChecker.updateDistance(mAllDistance, location.getTime());
                EventBus.getDefault().post(new LocationUpdateModel(location, mAllDistance, mCurrentSpeed));
                mLastLocation = location;
                mSharedPreferencesHelper.setDistanceInBackground(mAllDistance);

                Waypoint waypoint = new Waypoint(null, (float)location.getLatitude(), (float)location.getLongitude());
                waypoint.setTime(new Date(location.getTime()));
                mWaypoints.add(waypoint);
                if(mGPXUpdater != null)
                {
                    mGPXUpdater.addWaypoint(waypoint);
                }
            }


        };
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        long lastTime = mSharedPreferencesHelper.getCurrentTime();
        if(lastTime != 0)
        {
            mAllDistance = mSharedPreferencesHelper.getCurrentDistance();
        }
        mSharedPreferencesHelper.setTrackingActive(true);
        mFusedLocationClient.requestLocationUpdates
                (getLocationRequest(), mLocationCallback,
                        Looper.getMainLooper() );
        mTimeCounterUseCase = new TimeCounterUseCase(lastTime, System.currentTimeMillis());
        mTimeCounterUseCase.registerListener(this);
    }


    private void kilometersCompletedAction(int kmNumber)
    {
        vibrate();
        Toast.makeText(getApplicationContext(), "You've completed " + kmNumber + " kilometers!", Toast.LENGTH_SHORT).show();
        long seconds = mCurrentSeconds;
        String timeTextSpeech = "";
        long secondsT = (seconds - (seconds/60)*60);
        long minutesT = ((seconds/60) - (seconds/3600)*3600);
        long hoursT = (seconds/3600);

        if(hoursT != 0)
        {
            timeTextSpeech+= hoursT + " hours, ";
        }


        timeTextSpeech+= minutesT + " minutes, ";
        timeTextSpeech+= secondsT + " seconds";



        mTextToSpeech.speak("You have completed " + kmNumber + " kilometers!"
                + " Your time is " + timeTextSpeech + "!",TextToSpeech.QUEUE_FLUSH,null);
    }



    private void stopTrackingLocation() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mTimeCounterUseCase.unregisterListener(this);
        mTimeCounterUseCase.stopCounting();
        mSharedPreferencesHelper.setCurrentTime(mCurrentSeconds);
        mSharedPreferencesHelper.setDistance(mAllDistance);
        mSharedPreferencesHelper.setTrackingActive(false);
        EventBus.getDefault().post(new TrackingStopUpdate(false));
        mGPXUseCase.writeToGpx(mWaypoints, mTrainingId + ".gpx");
        if(mGPXUpdater != null)
        {
            mGPXUpdater.stopUpdating();
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(ConstAndStaticMethods.TRACKING_INTERVAL);
        locationRequest.setFastestInterval(ConstAndStaticMethods.TRACKING_FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onTimeUpdate(long currentSeconds) {
        mCurrentSeconds = currentSeconds;
        EventBus.getDefault().post(new TimeUpdateModel(currentSeconds));
        mSharedPreferencesHelper.setCurrentTimeInBackground(currentSeconds);
    }

    @Override
    public void onSpeedChange(double currentSpeed) {
        mCurrentSpeed = currentSpeed;
    }
}
package com.pawlowski.trackyouractivity.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.models.TrackingStopUpdate;
import com.pawlowski.trackyouractivity.service.TrackingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TrackingActivity extends AppCompatActivity implements TrackingViewMvc.OnControlButtonsClickListener {


    MapHelper mMapHelper;
    TrackingViewMvc mTrackingViewMvc;
    FusedLocationProviderClient mFusedLocationClient;
    PermissionHelper mPermissionHelper;
    SharedPreferencesHelper mSharedPreferencesHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackingViewMvc = new TrackingViewMvc(getLayoutInflater(), null, this);
        mTrackingViewMvc.registerListener(this);
        setContentView(mTrackingViewMvc.getRootView());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPermissionHelper = new PermissionHelper(mFusedLocationClient, this);
        mMapHelper = new MapHelper(mFusedLocationClient, mPermissionHelper);
        mSharedPreferencesHelper = new SharedPreferencesHelper(getSharedPreferences(Const.SHARED_PREFERENCES_NAME, MODE_MULTI_PROCESS));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_tracking);
        assert mapFragment != null;
        mapFragment.getMapAsync(mMapHelper);

        if(!mPermissionHelper.isTrackingPermissionGranted())
        {
            mPermissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                @Override
                public void onSuccess() {
                    if(!mPermissionHelper.isBackgroundTrackingPermissionGranted())
                    {
                        mPermissionHelper.requestBackgroundPermission(new PermissionHelper.OnPermissionReadyListener() {
                            @Override
                            public void onSuccess() {
                                startServiceIfActive();
                            }
                        });
                    }
                    else
                    {
                        if(mSharedPreferencesHelper.isTrackingActive())
                        {
                            startServiceIfActive();
                        }
                    }
                }
            });
        }
        else if(!mPermissionHelper.isBackgroundTrackingPermissionGranted())
        {
            mPermissionHelper.requestBackgroundPermission(new PermissionHelper.OnPermissionReadyListener() {
                @Override
                public void onSuccess() {
                    startServiceIfActive();
                }
            });
        }
        else
        {
            startServiceIfActive();
        }




    }

    private void startServiceIfActive()
    {
        if(mSharedPreferencesHelper.isTrackingActive())
        {
            float distance = mSharedPreferencesHelper.getCurrentDistance();
            long seconds = mSharedPreferencesHelper.getCurrentTime();
            mTrackingViewMvc.setTimeText(Const.convertSecondsToTimeTest(seconds/1000));
            mTrackingViewMvc.setDistanceText(Const.distanceMetersToKilometers(distance));
            mTrackingViewMvc.setSpeedText(0);
            mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PLAYED);
            if(!isServiceRunning())
            {
                startService();
            }
        }
        else
        {
            if(mSharedPreferencesHelper.getCurrentTime() != 0)
            {
                mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
                float distance = mSharedPreferencesHelper.getCurrentDistance();
                long seconds = mSharedPreferencesHelper.getCurrentTime();
                mTrackingViewMvc.setTimeText(Const.convertSecondsToTimeTest(seconds/1000));
                mTrackingViewMvc.setDistanceText(Const.distanceMetersToKilometers(distance));
                mTrackingViewMvc.setSpeedText(0);
            }
            else
            {
                mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.STOPPED);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdate(LocationUpdateModel locationUpdate)
    {
        mMapHelper.addLocationToMap(locationUpdate.getLocation());
        double distance = locationUpdate.getAllDistance();

        mTrackingViewMvc.setDistanceText(Const.distanceMetersToKilometers(distance));
        double speed = locationUpdate.getCurrentSpeed();
        speed*= 10;
        speed = Math.floor(speed);
        speed/= 10.0;
        mTrackingViewMvc.setSpeedText(speed);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeUpdate(TimeUpdateModel time)
    {
        mTrackingViewMvc.setTimeText(Const.convertSecondsToTimeTest(time.getTime()/1000));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackingStopUpdate(TrackingStopUpdate stopUpdate)
    {
        if(!stopUpdate.isTracking())
        {
            mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
        }
    }

    private boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startService()
    {
        startService(new Intent(TrackingActivity.this, TrackingService.class));
    }

    private void stopService()
    {
        sendBroadcast(new Intent(Const.INTENT_FILTER_STOP_TEXT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.handleRequestResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStartPauseClick() {
        switch (mTrackingViewMvc.getCurrentState())
        {
            case PAUSED:

            case STOPPED:
                startService();
                mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PLAYED);
                break;
            case PLAYED:
                stopService();
                //mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
                break;

        }
    }

    @Override
    public void onStopClick() {
        //TODO: Save training
        mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.STOPPED);
        mSharedPreferencesHelper.resetCurrentTraining();
    }
}
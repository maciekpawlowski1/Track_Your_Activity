package com.pawlowski.trackyouractivity.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.service.TrackingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TrackingActivity extends AppCompatActivity implements TrackingViewMvc.OnControlButtonsClickListener {


    MapHelper mapHelper;
    TrackingViewMvc trackingViewMvc;
    FusedLocationProviderClient mFusedLocationClient;
    PermissionHelper permissionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackingViewMvc = new TrackingViewMvc(getLayoutInflater(), null, this);
        trackingViewMvc.registerListener(this);
        setContentView(trackingViewMvc.getRootView());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        permissionHelper = new PermissionHelper(mFusedLocationClient, this);
        mapHelper = new MapHelper(mFusedLocationClient, permissionHelper);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_tracking);
        assert mapFragment != null;
        mapFragment.getMapAsync(mapHelper);


        if(!permissionHelper.isTrackingPermissionGranted())
        {
            permissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                @Override
                public void onSuccess() {
                    if(!permissionHelper.isBackgroundTrackingPermissionGranted())
                    {
                        permissionHelper.requestBackgroundPermission();
                    }
                    else
                    {
                        startService();
                    }
                }
            });
        }
        else if(!permissionHelper.isBackgroundTrackingPermissionGranted())
        {
            permissionHelper.requestBackgroundPermission();
        }
        else
        {
            startService();
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
        mapHelper.addLocationToMap(locationUpdate.getLocation());
        double distance = locationUpdate.getAllDistance()/1000.0;
        distance*= 10;
        distance = Math.floor(distance);
        distance/= 10.0;
        trackingViewMvc.setDistanceText(distance);
        Log.d("speed", locationUpdate.getCurrentSpeed()+"");
        double speed = locationUpdate.getCurrentSpeed();
        speed*= 10;
        speed = Math.floor(speed);
        speed/= 10.0;
        trackingViewMvc.setSpeedText(speed);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeUpdate(TimeUpdateModel time)
    {
        trackingViewMvc.setTimeText(Const.convertSecondsToTimeTest(time.getTime()/1000));
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
        permissionHelper.handleRequestResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStartPauseClick() {
        Log.d("startpause", "click");
    }

    @Override
    public void onStopClick() {
        Log.d("stop", "click");

    }
}
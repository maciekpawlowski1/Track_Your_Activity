package com.pawlowski.trackyouractivity.tracking;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.pawlowski.trackyouractivity.base.BaseObservable;
import com.pawlowski.trackyouractivity.consts.Const;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionHelper extends BaseObservable<PermissionHelper.OnPermissionReadyListener> {

    private final FusedLocationProviderClient mFusedLocationClient;
    private final Activity mActivity;

    public PermissionHelper(FusedLocationProviderClient fusedLocationProviderClient, Activity activity) {
        mFusedLocationClient = fusedLocationProviderClient;
        mActivity = activity;
    }


    public boolean isTrackingPermissionGranted()
    {
        return ActivityCompat.checkSelfPermission(mFusedLocationClient.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isBackgroundTrackingPermissionGranted()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(OnPermissionReadyListener onPermissionReadyListener)
    {
        registerListener(onPermissionReadyListener);
        ActivityCompat.requestPermissions(mActivity, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},
                Const.REQUEST_LOCATION_PERMISSION);
    }

    public void requestBackgroundPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(mActivity, new String[]
                            {Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    Const.REQUEST_LOCATION_PERMISSION2);
        }
    }

    public void handleRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
            if(requestCode == Const.REQUEST_LOCATION_PERMISSION)
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyListeners();
                } else {
                }
            }
    }

    @Override
    protected void notifyListeners() {
        for(OnPermissionReadyListener l:listeners)
        {
            l.onSuccess();
        }
        listeners.clear();
    }


    interface OnPermissionReadyListener
    {
        void onSuccess();
    }
}

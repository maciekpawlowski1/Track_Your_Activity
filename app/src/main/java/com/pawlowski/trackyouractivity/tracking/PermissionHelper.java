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

public class PermissionHelper {

    private final FusedLocationProviderClient mFusedLocationClient;
    private final Activity mActivity;
    private final List<OnPermissionReadyListener> listenersNormal = new ArrayList<>();
    private final List<OnPermissionReadyListener> listenersBackground = new ArrayList<>();


    public PermissionHelper(FusedLocationProviderClient fusedLocationProviderClient, Activity activity) {
        mFusedLocationClient = fusedLocationProviderClient;
        mActivity = activity;
    }

    public void registerNormalListener(OnPermissionReadyListener listener)
    {
        listenersNormal.add(listener);
    }

    public void registerBackgroundListener(OnPermissionReadyListener listener)
    {
        listenersBackground.add(listener);
    }


    public boolean isTrackingPermissionGranted()
    {
        return ActivityCompat.checkSelfPermission(mFusedLocationClient.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isBackgroundTrackingPermissionGranted()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            return ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED;
        }
        else
            return true;
    }

    public void requestPermission(OnPermissionReadyListener onPermissionReadyListener)
    {
        registerNormalListener(onPermissionReadyListener);
        ActivityCompat.requestPermissions(mActivity, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},
                Const.REQUEST_LOCATION_PERMISSION);
    }

    public void requestBackgroundPermission(OnPermissionReadyListener onPermissionReadyListener)
    {
        registerBackgroundListener(onPermissionReadyListener);
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
                    notifyNormalListeners();
                } else {
                    Toast.makeText(mActivity.getApplicationContext(), "You have to give localisation tracking permission to track your training!", Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == Const.REQUEST_LOCATION_PERMISSION2)
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyBackgroundListeners();
                } else {
                    Toast.makeText(mActivity.getApplicationContext(), "You have to give background localisation tracking permission also to track your training!", Toast.LENGTH_LONG).show();
                }
            }
    }

    private void notifyNormalListeners() {
        for(OnPermissionReadyListener l:listenersNormal)
        {
            l.onSuccess();
        }
        listenersNormal.clear();
    }

    private void notifyBackgroundListeners() {
        for(OnPermissionReadyListener l:listenersBackground)
        {
            l.onSuccess();
        }
        listenersBackground.clear();
    }


    interface OnPermissionReadyListener
    {
        void onSuccess();
    }
}

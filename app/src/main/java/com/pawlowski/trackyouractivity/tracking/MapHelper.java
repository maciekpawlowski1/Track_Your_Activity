package com.pawlowski.trackyouractivity.tracking;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.gpx.GPXUseCase;
import com.urizev.gpx.beans.Waypoint;

import java.util.List;

import androidx.annotation.NonNull;

public class MapHelper implements OnMapReadyCallback {
    final Object LOCK = new Object();
    GoogleMap mMap = null;
    Location mLastLocation;
    private final FusedLocationProviderClient mFusedLocationClient;
    private final PermissionHelper mPermissionHelper;
    private String mTrainingKey;
    private final Context mAppContext;
    private final boolean mCurrentlyTracking;

    public MapHelper(@NonNull FusedLocationProviderClient mFusedLocationClient, @NonNull PermissionHelper permissionHelper, String trainingKey) {
        this.mFusedLocationClient = mFusedLocationClient;
        mPermissionHelper = permissionHelper;
        mTrainingKey = trainingKey;
        mCurrentlyTracking = true;
        mAppContext = mFusedLocationClient.getApplicationContext();
    }

    public MapHelper(String trainingKey, Context appContext) {
        mTrainingKey = trainingKey;
        mAppContext = appContext;
        mCurrentlyTracking = false;
        mFusedLocationClient = null;
        mPermissionHelper = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(18);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        if(mCurrentlyTracking && mPermissionHelper != null)
        {
            if (mTrainingKey == null && mPermissionHelper.isTrackingPermissionGranted()) {
                try {
                    showCurrentLocation();
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
            else if (mPermissionHelper.isTrackingPermissionGranted())
            {
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
        }



        if(mTrainingKey != null)
        {
            Log.d("reading", "reading " + mTrainingKey);
            new GPXUseCase(mAppContext.getFilesDir()).readFromGpxTask(mTrainingKey + ".gpx").addOnSuccessListener(new OnSuccessListener<List<Waypoint>>() {
                @Override
                public void onSuccess(List<Waypoint> waypoints) {
                    Log.d("reading", "success: " + waypoints.size());
                    addManyWaypointsToMap(waypoints);

                }
            });
        }

    }

    public void addLocationToMap(Location nextLocation)
    {
        if(mMap != null)
        {
            synchronized (LOCK)
            {
                LatLng l2 = new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(l2));
                if(mLastLocation != null)
                {
                    LatLng l1 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.addPolyline(new PolylineOptions().add(l1, l2).width(8).color(Color.rgb(251, 116, 69)));
                }
            }
        }
        mLastLocation = nextLocation;
    }

    public void addManyWaypointsToMap(List<Waypoint> waypoints)
    {
        if(mMap != null)
        {
            synchronized (LOCK)
            {
                mMap.clear();
                Waypoint lastWaypoint = null;
                for(Waypoint waypoint:waypoints)
                {
                    if(lastWaypoint != null)
                    {
                        LatLng l1 = new LatLng(lastWaypoint.getLatitude(), lastWaypoint.getLongitude());
                        LatLng l2 = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
                        mMap.addPolyline(new PolylineOptions().add(l1, l2).width(8).color(Color.rgb(251, 116, 69)));
                    }
                    lastWaypoint = waypoint;
                }
                if(lastWaypoint != null)
                {
                    LatLng l = new LatLng(lastWaypoint.getLatitude(), lastWaypoint.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
                    if(mLastLocation != null)
                    {
                        LatLng l1 = new LatLng(lastWaypoint.getLatitude(), lastWaypoint.getLongitude());
                        LatLng l2 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    }
                }
            }


        }

    }

    public void clearMap()
    {
        if(mMap != null)
        {
            synchronized (LOCK)
            {
                mMap.clear();

            }
        }
    }

    private void showCurrentLocation() throws SecurityException
    {
        if(mMap != null)
        {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null)
                        return;
                    LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(l));

                }
            });
        }


    }
}

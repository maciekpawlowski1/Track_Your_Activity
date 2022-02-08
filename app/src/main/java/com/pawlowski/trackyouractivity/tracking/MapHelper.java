package com.pawlowski.trackyouractivity.tracking;

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
    private final int mTrainingId;

    public MapHelper(FusedLocationProviderClient mFusedLocationClient, PermissionHelper permissionHelper, int trainingId) {
        this.mFusedLocationClient = mFusedLocationClient;
        mPermissionHelper = permissionHelper;
        mTrainingId = trainingId;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(18);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        if (mTrainingId == -1 && mPermissionHelper.isTrackingPermissionGranted()) {
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


        if(mTrainingId != -1)
        {
            new GPXUseCase(mFusedLocationClient.getApplicationContext().getFilesDir()).readFromGpxTask(mTrainingId + ".gpx").addOnSuccessListener(new OnSuccessListener<List<Waypoint>>() {
                @Override
                public void onSuccess(List<Waypoint> waypoints) {
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

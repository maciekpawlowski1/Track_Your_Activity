package com.pawlowski.trackyouractivity.tracking;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private final String mTrainingKey;
    private final boolean mCurrentlyTracking;
    private final GPXUseCase mGpxUseCase;

    public MapHelper(@NonNull FusedLocationProviderClient mFusedLocationClient, @NonNull PermissionHelper permissionHelper, String trainingKey, GPXUseCase gpxUseCase) {
        this.mFusedLocationClient = mFusedLocationClient;
        mPermissionHelper = permissionHelper;
        mTrainingKey = trainingKey;
        this.mGpxUseCase = gpxUseCase;
        mCurrentlyTracking = true;
    }

    public MapHelper(String trainingKey, GPXUseCase gpxUseCase) {
        mTrainingKey = trainingKey;
        mCurrentlyTracking = false;
        mFusedLocationClient = null;
        mPermissionHelper = null;
        this.mGpxUseCase = gpxUseCase;
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
                startShowingLocation();
            }
        }



        readGpxAndUpdateMap();

    }

    public void startShowingLocation()
    {
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    public void readGpxAndUpdateMap()
    {
        if(mTrainingKey != null && mMap != null)
        {
            mGpxUseCase.readFromGpxTask(mTrainingKey + ".gpx").addOnSuccessListener(new OnSuccessListener<>() {
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
                    mLastLocation = new Location("reverseGeocoded");
                    mLastLocation.setLatitude(lastWaypoint.getLatitude());
                    mLastLocation.setLongitude(lastWaypoint.getLongitude());
                    mLastLocation.setTime(lastWaypoint.getTime().getTime());
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
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<>() {
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

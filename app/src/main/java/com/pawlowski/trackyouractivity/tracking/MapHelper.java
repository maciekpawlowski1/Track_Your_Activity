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

import androidx.annotation.NonNull;

public class MapHelper implements OnMapReadyCallback {

    GoogleMap mMap = null;
    Location mLastLocation;
    private final FusedLocationProviderClient mFusedLocationClient;
    private final PermissionHelper mPermissionHelper;

    public MapHelper(FusedLocationProviderClient mFusedLocationClient, PermissionHelper permissionHelper) {
        this.mFusedLocationClient = mFusedLocationClient;
        mPermissionHelper = permissionHelper;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(18);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        if (mPermissionHelper.isTrackingPermissionGranted()) {
            try {
                showCurrentLocation();
            } catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            mPermissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                @Override
                public void onSuccess() {
                    if (mPermissionHelper.isTrackingPermissionGranted()) {
                        try {
                            showCurrentLocation();
                        } catch (SecurityException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void addLocationToMap(Location nextLocation)
    {
        LatLng l2 = new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(l2));
        if(mLastLocation != null)
        {
            LatLng l1 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addPolyline(new PolylineOptions().add(l1, l2).width(8).color(Color.rgb(251, 116, 69)));
        }

        mLastLocation = nextLocation;
    }

    private void showCurrentLocation() throws SecurityException
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
        mMap.setMyLocationEnabled(true);

    }


}

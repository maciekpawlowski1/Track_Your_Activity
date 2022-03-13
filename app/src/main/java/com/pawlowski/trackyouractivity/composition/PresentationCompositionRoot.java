package com.pawlowski.trackyouractivity.composition;

import com.pawlowski.trackyouractivity.MyApplication;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.gpx.GPXUseCase;
import com.pawlowski.trackyouractivity.tracking.MapHelper;
import com.pawlowski.trackyouractivity.tracking.PermissionHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PresentationCompositionRoot {
    private final ActivityCompositionRoot activityCompositionRoot;
    private ViewMvcFactory viewMvcFactory = null;


    public PresentationCompositionRoot(ActivityCompositionRoot activityCompositionRoot) {
        this.activityCompositionRoot = activityCompositionRoot;
    }


    public SharedPreferencesHelper getSharedPreferencesHelper()
    {
        return activityCompositionRoot.getSharedPreferencesHelper();
    }

    public DBHandler getDBHandler()
    {
        return activityCompositionRoot.getDBHandler();
    }

    public FirebaseAuthHelper getFirebaseAuthHelper()
    {
        return activityCompositionRoot.getFirebaseAuthHelper();
    }

    public FirebaseDatabaseHelper getFirebaseDatabaseHelper()
    {
        return activityCompositionRoot.getFirebaseDatabaseHelper();
    }

    public ViewMvcFactory getViewMvcFactory()
    {
        if(viewMvcFactory == null)
            viewMvcFactory = new ViewMvcFactory(activityCompositionRoot.getLayoutInflater());

        return viewMvcFactory;
    }

    public GPXUseCase getGPXUseCase()
    {
        return activityCompositionRoot.getGPXUseCase();
    }

    public PermissionHelper getPermissionHelper()
    {
        return activityCompositionRoot.getPermissionHelper();
    }

    public MapHelper getMapHelper(String trainingKey, boolean isCurrentlyTracking, @Nullable PermissionHelper permissionHelper)
    {
        if(isCurrentlyTracking) {
            assert permissionHelper != null;
            return new MapHelper(activityCompositionRoot.getFusedLocationClient(), permissionHelper, trainingKey, getGPXUseCase());
        }
        else
            return new MapHelper(trainingKey, getGPXUseCase());
    }
}

package com.pawlowski.trackyouractivity.composition;

import com.pawlowski.trackyouractivity.MyApplication;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;

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
        return new FirebaseAuthHelper();
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
}

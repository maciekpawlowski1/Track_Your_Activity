package com.pawlowski.trackyouractivity.composition;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;

public class AppCompositionRoot {
    private final Context appContext;
    private DBHandler dbHandler = null;
    private FirebaseDatabaseHelper firebaseDatabaseHelper = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferencesHelper sharedPreferencesHelper = null;
    public AppCompositionRoot(Context appContext)
    {
        this.appContext = appContext;
    }

    public DBHandler getDBHandler() //TODO: Think does one DBHandler also for WorkManager jobs is a good idea
    {
        if(dbHandler == null)
        {
            dbHandler = new DBHandler(appContext);
        }
        return dbHandler;
    }

    private SharedPreferences getSharedPreferences() {
        if(sharedPreferences == null)
            sharedPreferences = appContext.getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences;
    }

    public FirebaseDatabaseHelper getFirebaseDatabaseHelper()
    {
        if(firebaseDatabaseHelper == null)
            firebaseDatabaseHelper = new FirebaseDatabaseHelper();

        return firebaseDatabaseHelper;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper()
    {
        if(sharedPreferencesHelper == null)
            sharedPreferencesHelper = new SharedPreferencesHelper(getSharedPreferences());

        return sharedPreferencesHelper;
    }
}

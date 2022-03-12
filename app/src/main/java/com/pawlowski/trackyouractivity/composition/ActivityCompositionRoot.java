package com.pawlowski.trackyouractivity.composition;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;

import com.pawlowski.trackyouractivity.MyApplication;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityCompositionRoot {

    private final AppCompatActivity activity;
    private final LayoutInflater layoutInflater;
    private AppCompositionRoot appCompositionRoot = null;


    public ActivityCompositionRoot(AppCompatActivity activity)
    {
        this.activity = activity;
        this.layoutInflater = activity.getLayoutInflater();
    }
    private AppCompositionRoot getAppCompositionRoot()
    {
        if(appCompositionRoot == null)
        {
            appCompositionRoot = ((MyApplication)activity.getApplication()).getAppCompositionRoot();
        }
        return appCompositionRoot;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return getAppCompositionRoot().getSharedPreferencesHelper();
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public DBHandler getDBHandler()
    {
        return getAppCompositionRoot().getDBHandler();
    }


    public FirebaseDatabaseHelper getFirebaseDatabaseHelper()
    {
        return getAppCompositionRoot().getFirebaseDatabaseHelper();
    }



}

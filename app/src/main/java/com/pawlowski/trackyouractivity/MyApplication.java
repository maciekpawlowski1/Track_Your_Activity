package com.pawlowski.trackyouractivity;

import android.app.Application;

import com.pawlowski.trackyouractivity.composition.AppCompositionRoot;

public class MyApplication extends Application {
    private AppCompositionRoot appCompositionRoot = null;

    public AppCompositionRoot getAppCompositionRoot() {
        if(appCompositionRoot == null)
        {
            appCompositionRoot = new AppCompositionRoot(getApplicationContext());
        }
        return appCompositionRoot;
    }
}

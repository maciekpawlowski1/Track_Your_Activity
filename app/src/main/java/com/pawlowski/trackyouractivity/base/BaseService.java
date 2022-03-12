package com.pawlowski.trackyouractivity.base;

import android.app.Service;

import com.pawlowski.trackyouractivity.MyApplication;
import com.pawlowski.trackyouractivity.composition.AppCompositionRoot;

abstract public class BaseService extends Service {
    private AppCompositionRoot appCompositionRoot;

    public AppCompositionRoot getAppCompositionRoot() {
        if(appCompositionRoot == null)
            appCompositionRoot = ((MyApplication)getApplication()).getAppCompositionRoot();

        return appCompositionRoot;
    }
}

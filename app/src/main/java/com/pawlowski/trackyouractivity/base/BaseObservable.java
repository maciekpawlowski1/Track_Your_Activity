package com.pawlowski.trackyouractivity.base;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseObservable <ListenerType>{
    protected List<ListenerType> listeners = new ArrayList<>();

    public void registerListener(ListenerType listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(ListenerType listener)
    {
        listeners.remove(listener);
    }

    abstract protected void notifyListeners();

}

package com.pawlowski.trackyouractivity.base;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public abstract class BaseObservableViewMvc<ListenerType> extends BaseViewMvc{
    protected List<ListenerType> listeners = new ArrayList<>();

    public void registerListener(ListenerType listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(ListenerType listener)
    {
        listeners.remove(listener);
    }

    abstract protected void notifyListeners(@Nullable Integer which);
}

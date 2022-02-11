package com.pawlowski.trackyouractivity.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.overview.HistoryAdapter;


public class HistoryFragment extends Fragment implements HistoryViewMvc.HistoryButtonsClickListener {


    private HistoryViewMvc mViewMvc;
    private HistoryAdapter mHistoryAdapter;
    private DBHandler mDbHandler;
    private MainViewMvc mMainActivityViewMvc;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryFragment(MainViewMvc mainActivityViewMvc)
    {
        mMainActivityViewMvc = mainActivityViewMvc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewMvc = new HistoryViewMvc(inflater, container);

        mViewMvc.registerListener(this);
        mHistoryAdapter = new HistoryAdapter(getContext());
        mDbHandler = new DBHandler(getContext());
        mViewMvc.setRecyclerAdapter(mHistoryAdapter);
        mHistoryAdapter.setTrainings(mDbHandler.getAllTrainings());
        return mViewMvc.getRootView();
    }

    @Override
    public void onMenuButtonClick() {
        mMainActivityViewMvc.showNavigation();
    }
}
package com.pawlowski.trackyouractivity.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.base.BaseFragment;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.overview.HistoryAdapter;

import androidx.annotation.NonNull;


public class HistoryFragment extends BaseFragment implements HistoryViewMvc.HistoryButtonsClickListener {


    private HistoryViewMvc mViewMvc;
    private HistoryAdapter mHistoryAdapter;
    private DBHandler mDbHandler;
    private MainViewMvc mMainActivityViewMvc;
    private String mAccountKey;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryFragment(MainViewMvc mainActivityViewMvc, String accountKey)
    {
        mMainActivityViewMvc = mainActivityViewMvc;
        mAccountKey = accountKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewMvc = getCompositionRoot().getViewMvcFactory().getHistoryViewMvc(container);

        mViewMvc.registerListener(this);
        mHistoryAdapter = new HistoryAdapter(getContext());
        mDbHandler = getCompositionRoot().getDBHandler();
        mViewMvc.setRecyclerAdapter(mHistoryAdapter);
        mHistoryAdapter.setTrainings(mDbHandler.getAllTrainings(mAccountKey));
        return mViewMvc.getRootView();
    }

    @Override
    public void onMenuButtonClick() {
        mMainActivityViewMvc.showNavigation();
    }

    public static HistoryFragment newInstance(MainViewMvc mainViewMvc, String accountKey)
    {
        return new HistoryFragment(mainViewMvc, accountKey);
    }
}
package com.pawlowski.trackyouractivity.overview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.models.TrackingStopUpdate;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.tracking.TrackingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;


public class OverviewFragment extends Fragment implements OverviewViewMvc.OverviewButtonsListener {

    MainViewMvc mMainActivityViewMvc;
    OverviewViewMvc mViewMvc;
    HistoryAdapter mHistoryAdapter;
    SharedPreferencesHelper mSharedPreferences;
    private DBHandler mDbHandler;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public OverviewFragment(MainViewMvc mainActivityViewMvc) {
        this.mMainActivityViewMvc = mainActivityViewMvc;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHandler = new DBHandler(getContext());
        mSharedPreferences = new SharedPreferencesHelper(requireActivity().getSharedPreferences(Const.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS));

    }

    @Override
    public void onStart() {
        super.onStart();
        if(mSharedPreferences.getCurrentTime() != 0)
        {
            mViewMvc.setCurrentTrainingTime(Const.convertSecondsToTimeTest(mSharedPreferences.getCurrentTime()/1000));
            mViewMvc.setCurrentTrainingDistance(Const.distanceMetersToKilometers(mSharedPreferences.getCurrentDistance())+"");
            mViewMvc.setCurrentTrainingTypeImage(mDbHandler.getTypeOfCurrentTraining());
            EventBus.getDefault().register(this);
            mViewMvc.showCurrentActivityPanel();
        }
        else
        {
            mViewMvc.hideCurrentActivityAndShowStartActivityPanel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewMvc = new OverviewViewMvc(inflater, container);

        mViewMvc.registerListener(this);

        mHistoryAdapter = new HistoryAdapter();
        mViewMvc.setRecyclerAdapter(mHistoryAdapter);
        mHistoryAdapter.setTrainings(mDbHandler.getLast3Trainings()); //TODO: Move to background thread

        return mViewMvc.getRootView();
    }

    @Override
    public void onMenuButtonClick() {
        mMainActivityViewMvc.showNavigation();
    }

    @Override
    public void onCurrentTrainingCardClick() {
        Intent i = new Intent(getContext(), TrackingActivity.class);
        i.putExtra("training_type", -1);
        startActivity(i);
    }

    @Override
    public void onTrainingStartClick(TrainingModel.TrainingType trainingType) {
        Intent i = new Intent(getContext(), TrackingActivity.class);
        i.putExtra("training_type", trainingType.ordinal());
        startActivity(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDistanceUpdate(LocationUpdateModel locationUpdate)
    {
        mViewMvc.setCurrentTrainingDistance(Const.distanceMetersToKilometers(locationUpdate.getAllDistance())+"");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeUpdate(TimeUpdateModel time)
    {
        mViewMvc.setCurrentTrainingTime(Const.convertSecondsToTimeTest(time.getTime()/1000));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackingStopUpdate(TrackingStopUpdate stopUpdate)
    {
        //TODO: Change color of dot
    }
}
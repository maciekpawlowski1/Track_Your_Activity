package com.pawlowski.trackyouractivity.overview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.history.HistoryFragment;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.models.TrackingStopUpdate;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.models.TrainingsDownloadedUpdateModel;
import com.pawlowski.trackyouractivity.tracking.TrackingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class OverviewFragment extends Fragment implements OverviewViewMvc.OverviewButtonsListener {

    private MainViewMvc mMainActivityViewMvc;
    private OverviewViewMvc mViewMvc;
    private HistoryAdapter mHistoryAdapter;
    private SharedPreferencesHelper mSharedPreferences;
    private DBHandler mDbHandler;
    private String mAccountKey;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public OverviewFragment(MainViewMvc mainActivityViewMvc, String accountKey) {
        this.mMainActivityViewMvc = mainActivityViewMvc;
        mAccountKey = accountKey;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHandler = new DBHandler(getContext());
        mSharedPreferences = new SharedPreferencesHelper(requireActivity().getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS));
    }

    @Override
    public void onStart() {
        super.onStart();

        mHistoryAdapter.setTrainings(mDbHandler.getLast3Trainings(mAccountKey)); //TODO: Move to background thread

        EventBus.getDefault().register(this);


        bindWeeklyGoal();

        if(mSharedPreferences.getCurrentTime() != 0)
        {
            mViewMvc.setCurrentTrainingTime(ConstAndStaticMethods.convertSecondsToTimeTest(mSharedPreferences.getCurrentTime()/1000));
            mViewMvc.setCurrentTrainingDistance(ConstAndStaticMethods.distanceMetersToKilometers(mSharedPreferences.getCurrentDistance())+"");
            mViewMvc.setCurrentTrainingTypeImage(mDbHandler.getTypeOfCurrentTraining());
            mViewMvc.showCurrentActivityPanel();

            if(!mSharedPreferences.isTrackingActive())
            {
                //TODO: Change color of dot (yellow)
            }
            else
            {
                //TODO: Change color of dot (green)
            }
        }
        else
        {
            mViewMvc.hideCurrentActivityAndShowStartActivityPanel();
        }
    }

    private void bindWeeklyGoal()
    {
        int alreadyDone = mDbHandler.getWeeklyKm(getWeekStartDate(Calendar.getInstance().getTime()).getTime(),
                getWeekEndDate(Calendar.getInstance().getTime()).getTime());


        int weeklyGoal = mSharedPreferences.getWeeklyGoal();
        mViewMvc.bindWeeklyGoal(weeklyGoal, alreadyDone, Math.max(weeklyGoal - alreadyDone, 0));
    }

    private static Date getWeekStartDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTime();
    }


    private static Date getWeekEndDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(getWeekStartDate(date));
        cal.add(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
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

        mHistoryAdapter = new HistoryAdapter(getContext());
        mViewMvc.setRecyclerAdapter(mHistoryAdapter);

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

    @Override
    public void onMoreHistoryClick() {
        mMainActivityViewMvc.loadFragment(new HistoryFragment(mMainActivityViewMvc, mAccountKey), requireActivity().getSupportFragmentManager(), false);
        mMainActivityViewMvc.checkItem(R.id.history_nav_menu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDistanceUpdate(LocationUpdateModel locationUpdate)
    {
        mViewMvc.setCurrentTrainingDistance(ConstAndStaticMethods.distanceMetersToKilometers(locationUpdate.getAllDistance())+"");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrainingsDownloadedUpdate(TrainingsDownloadedUpdateModel trainingsUpdate)
    {
        mHistoryAdapter.setTrainings(mDbHandler.getLast3Trainings(mAccountKey)); //TODO: Move to background thread
        bindWeeklyGoal();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeUpdate(TimeUpdateModel time)
    {
        mViewMvc.setCurrentTrainingTime(ConstAndStaticMethods.convertSecondsToTimeTest(time.getTime()/1000));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackingStopUpdate(TrackingStopUpdate stopUpdate)
    {
        //TODO: Change color of dot
    }
}
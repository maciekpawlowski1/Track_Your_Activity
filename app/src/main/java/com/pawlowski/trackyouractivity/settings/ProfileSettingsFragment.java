package com.pawlowski.trackyouractivity.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Operation;
import androidx.work.WorkInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.overview.OverviewFragment;
import com.pawlowski.trackyouractivity.upload_job.WorkHelper;


public class ProfileSettingsFragment extends Fragment implements ProfileSettingsViewMvc.ProfileSettingsButtonsClickListener {

    private ProfileSettingsViewMvc mViewMvc;
    private SharedPreferencesHelper mSharedPreferences;
    private MainViewMvc mMainViewMvc;
    private String mAccountKey;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private FirebaseAuthHelper mFirebaseAuthHelper;

    public ProfileSettingsFragment() {
        // Required empty public constructor
    }


    public ProfileSettingsFragment(MainViewMvc mainViewMvc, String accountKey) {
        mMainViewMvc = mainViewMvc;
        mAccountKey = accountKey;
        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper();
        mFirebaseAuthHelper = new FirebaseAuthHelper();
    }






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = new SharedPreferencesHelper(requireActivity().getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS));

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewMvc = new ProfileSettingsViewMvc(inflater, container);
        mViewMvc.registerListener(this);



        return mViewMvc.getRootView();
    }

    @Override
    public void onSaveClick() {
        String date = mViewMvc.getDateOfBirth().trim();
        String name = mViewMvc.getName().trim();
        int weight = mViewMvc.getWeight();
        int goal = mViewMvc.getGoal();

        boolean correct = true;
        if(!ConstAndStaticMethods.isNameCorrect(name))
        {
            correct = false;
            mViewMvc.setNameError("Incorrect name");
        }
        if(!ConstAndStaticMethods.isDateCorrect(date))
        {
            correct = false;
            mViewMvc.setBirthdayError("Incorrect date");
        }
        if(weight == -1 || weight == 0)
        {
            correct = false;
            mViewMvc.setWeightError("Incorrect number!");
        }
        if(goal == -1)
        {
            correct = false;
            mViewMvc.setGoalError("Incorrect number!");
        }

        if(correct)
        {
            mFirebaseDatabaseHelper.addUserInfo(mFirebaseAuthHelper.getCurrentUser().getUid(), name, date, goal, weight);
            mSharedPreferences.setWeeklyGoal(goal);
            mSharedPreferences.setProfileSaved(true);
            mMainViewMvc.loadFragment(new OverviewFragment(mMainViewMvc, mAccountKey), requireActivity().getSupportFragmentManager(), false);
            //TODO: Save
        }
    }
}
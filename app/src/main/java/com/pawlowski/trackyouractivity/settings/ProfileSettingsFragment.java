package com.pawlowski.trackyouractivity.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;


public class ProfileSettingsFragment extends Fragment implements ProfileSettingsViewMvc.ProfileSettingsButtonsClickListener {

    private ProfileSettingsViewMvc mViewMvc;
    private SharedPreferencesHelper mSharedPreferences;

    public ProfileSettingsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = new SharedPreferencesHelper(requireActivity().getSharedPreferences(Const.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS));

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
        if(!Const.isNameCorrect(name))
        {
            correct = false;
            mViewMvc.setNameError("Incorrect name");
        }
        if(!Const.isDateCorrect(date))
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
            mSharedPreferences.setWeeklyGoal(goal);
            //TODO: Save
        }
    }
}
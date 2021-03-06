package com.pawlowski.trackyouractivity.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.base.BaseFragment;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.overview.OverviewFragment;

import androidx.annotation.NonNull;


public class ProfileSettingsFragment extends BaseFragment implements ProfileSettingsViewMvc.ProfileSettingsButtonsClickListener {

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

    }






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getCompositionRoot().getSharedPreferencesHelper();
        mFirebaseDatabaseHelper = getCompositionRoot().getFirebaseDatabaseHelper();
        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewMvc = getCompositionRoot().getViewMvcFactory().getProfileSettingsViewMvc(container, mMainViewMvc);
        mViewMvc.registerListener(this);



        if(mSharedPreferences.isProfileSaved())
        {
            mViewMvc.setVisibilityOfMenuButton(true);
            mViewMvc.bindSavedValues(mSharedPreferences.getName(), mSharedPreferences.getWeeklyGoal(),mSharedPreferences.getWeight(), mSharedPreferences.getDateOfBirth());
        }
        else
        {
            mViewMvc.setVisibilityOfMenuButton(false);
        }

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
            //TODO: Wait until saving finish, show progress dialog and then only save to SharedPreferences
            mSharedPreferences.setWeeklyGoal(goal);
            mSharedPreferences.setWeight(weight);
            mSharedPreferences.setName(name);
            mSharedPreferences.setDateOfBirth(date);
            mSharedPreferences.setProfileSaved(true);
            mMainViewMvc.setHeaderNameText(name);
            mMainViewMvc.checkItem(R.id.overview_nav_menu);
            mMainViewMvc.loadFragment(OverviewFragment.newInstance(mMainViewMvc, mAccountKey), requireActivity().getSupportFragmentManager(), false);
        }
    }

    public static ProfileSettingsFragment newInstance(MainViewMvc mainViewMvc, String accountKey)
    {
        return new ProfileSettingsFragment(mainViewMvc, accountKey);
    }
}
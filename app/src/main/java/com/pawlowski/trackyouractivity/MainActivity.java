package com.pawlowski.trackyouractivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.base.BaseActivity;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.download_job.DownloadWorkHelper;
import com.pawlowski.trackyouractivity.history.HistoryFragment;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.models.UserModel;
import com.pawlowski.trackyouractivity.overview.OverviewFragment;
import com.pawlowski.trackyouractivity.settings.ProfileSettingsFragment;
import com.pawlowski.trackyouractivity.tracking.TrackingActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

public class MainActivity extends BaseActivity {

    private MainViewMvc mViewMvc;
    private SharedPreferencesHelper mSharedPreferences;
    private String mAccountKey;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private FirebaseAuthHelper mFirebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getMainViewMvc(null, this);

        setContentView(mViewMvc.getRootView());

        mSharedPreferences = getCompositionRoot().getSharedPreferencesHelper();

        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();
        mAccountKey = mFirebaseAuthHelper.getCurrentUser().getUid();
        mFirebaseDatabaseHelper = getCompositionRoot().getFirebaseDatabaseHelper();



        mViewMvc.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.overview_nav_menu)
                {
                    mViewMvc.loadFragment(OverviewFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.overview_nav_menu);
                }
                else if (item.getItemId() == R.id.track_nav_menu)
                {
                    TrackingActivity.launch(MainActivity.this, TrainingModel.TrainingType.RUNNING.ordinal());
                    mViewMvc.hideNavigation();
                }
                else if (item.getItemId() == R.id.history_nav_menu)
                {
                    mViewMvc.loadFragment(HistoryFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.history_nav_menu);
                }
                else if(item.getItemId() == R.id.settings_nav_menu)
                {
                    mViewMvc.loadFragment(ProfileSettingsFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.settings_nav_menu);
                }
                else if(item.getItemId() == R.id.sign_out_nav_menu)
                {
                    if(mSharedPreferences.isTrackingActive())
                    {
                        TrackingActivity.launch(MainActivity.this, TrainingModel.TrainingType.RUNNING.ordinal());
                        mViewMvc.hideNavigation();
                        Toast.makeText(getApplicationContext(), "You have to finish your training first!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        mSharedPreferences.resetPersonValues();
                        mFirebaseAuthHelper.signOut();
                        SignInActivity.launch(MainActivity.this);
                        finish();
                    }

                }

                    return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if((bundle != null && bundle.getBoolean("start_with_settings", false)) || !mSharedPreferences.isProfileSaved())
        {
            showProgressDialog("Please wait...");
            mFirebaseDatabaseHelper.getUserInfo(mAccountKey).addOnCompleteListener(new OnCompleteListener<>() {
                @Override
                public void onComplete(@NonNull Task<UserModel> task) {
                    if(task.isSuccessful())
                    {
                        UserModel user = task.getResult();
                        mSharedPreferences.setWeeklyGoal(user.getGoal());
                        mSharedPreferences.setWeight(user.getWeight());
                        mSharedPreferences.setName(user.getName());
                        mSharedPreferences.setDateOfBirth(user.getDateOfBirth());
                        mSharedPreferences.setProfileSaved(true);
                        mViewMvc.loadFragment(OverviewFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                        mViewMvc.checkItem(R.id.overview_nav_menu);
                    }
                    else
                    {
                        mViewMvc.loadFragment(ProfileSettingsFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                        mViewMvc.checkItem(R.id.settings_nav_menu);
                    }
                    hideProgressDialog();
                }
            });
        }
        else
        {
            mViewMvc.setHeaderNameText(mSharedPreferences.getName());
            mViewMvc.loadFragment(OverviewFragment.newInstance(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
            mViewMvc.checkItem(R.id.overview_nav_menu);
            if(mSharedPreferences.isTrackingActive())
            {
                TrackingActivity.launch(this, TrainingModel.TrainingType.RUNNING.ordinal());
            }
        }



        checkForUpdatesIfNeeded();
    }

    public void checkForUpdatesIfNeeded()
    {
        Log.d("checkForUpdatesIfNeeded", "Method called");
        long lastCheck = mSharedPreferences.getLastUpdatesCheckTime();
        long now = System.currentTimeMillis();
        if(now - lastCheck > ConstAndStaticMethods.UPDATE_CHECK_DELTA_TIME_IN_SECONDS * 1000)
        {
            Log.d("checkForUpdatesIfNeeded", "Checking...");
            Handler handler = new Handler(getMainLooper());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> trainingKeys = getCompositionRoot().getDBHandler().getAllTrainingsKeys(mAccountKey);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFirebaseDatabaseHelper.getKeysToDownload(mAccountKey, trainingKeys).addOnSuccessListener(new OnSuccessListener<>() {
                                @Override
                                public void onSuccess(List<String> strings) {
                                    mSharedPreferences.setLastUpdatesCheckTime(now);
                                    if(strings.size() > 0)
                                    {
                                        Log.d("checkForUpdatesIfNeeded", "Found to download: " + strings.size());
                                        startDownloadWorker(strings);

                                    }
                                    else
                                    {
                                        Log.d("checkForUpdatesIfNeeded", "No updates needed");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });

                }
            }).start();

        }
        else
        {
            Log.d("checkForUpdatesIfNeeded", "Don't need to check");
        }
    }

    public void startDownloadWorker(List<String> trainingKeys)
    {
        new DownloadWorkHelper().startWorkIfNotExists(getApplicationContext(), mAccountKey, trainingKeys).observe(MainActivity.this, new Observer<>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                Log.d("DownloadWorkerState", workInfo.getState().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        MenuItem checkedItem = mViewMvc.getCheckedItem();
        if((checkedItem == null || checkedItem.getItemId() != R.id.settings_nav_menu))
        {
            if(!mViewMvc.showNavigation())
            {
                super.onBackPressed();
            }
        }
        else if(checkedItem.getItemId() == R.id.settings_nav_menu)
        {
            if(mSharedPreferences.isProfileSaved())
            {
                if(!mViewMvc.showNavigation())
                    super.onBackPressed();
            }

        }
    }

    public static void launch(Context context, boolean startWithSettings)
    {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("start_with_settings", startWithSettings);
        context.startActivity(i);
    }
}
package com.pawlowski.trackyouractivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.base.BaseActivity;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

public class MainActivity extends BaseActivity {

    private MainViewMvc mViewMvc;
    private SharedPreferencesHelper mSharedPreferences;
    private String mAccountKey;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new MainViewMvc(getLayoutInflater(), null, this);

        setContentView(mViewMvc.getRootView());

        mSharedPreferences = new SharedPreferencesHelper(getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, MODE_MULTI_PROCESS));

        mAccountKey = new FirebaseAuthHelper().getCurrentUser().getUid();
        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper();


        mViewMvc.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.overview_nav_menu)
                {
                    mViewMvc.loadFragment(new OverviewFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.overview_nav_menu);
                }
                else if (item.getItemId() == R.id.track_nav_menu)
                {
                    Intent i = new Intent(MainActivity.this, TrackingActivity.class);
                    i.putExtra("training_type", TrainingModel.TrainingType.RUNNING);
                    startActivity(i);
                    mViewMvc.hideNavigation();
                }
                else if (item.getItemId() == R.id.history_nav_menu)
                {
                    mViewMvc.loadFragment(new HistoryFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.history_nav_menu);
                }
                else if(item.getItemId() == R.id.settings_nav_menu)
                {
                    mViewMvc.loadFragment(new ProfileSettingsFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.history_nav_menu);
                }

                    return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if((bundle != null && bundle.getBoolean("start_with_settings", false)) || !mSharedPreferences.isProfileSaved())
        {
            //TODO: Save also other profile information
            showProgressDialog("Please wait...");
            mFirebaseDatabaseHelper.getUserInfo(mAccountKey).addOnCompleteListener(new OnCompleteListener<UserModel>() {
                @Override
                public void onComplete(@NonNull Task<UserModel> task) {
                    if(task.isSuccessful())
                    {
                        UserModel user = task.getResult();
                        mSharedPreferences.setWeeklyGoal(user.getGoal());
                        mSharedPreferences.setProfileSaved(true);
                        mViewMvc.loadFragment(new OverviewFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    }
                    else
                    {
                        mViewMvc.loadFragment(new ProfileSettingsFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
                    }
                    hideProgressDialog();
                }
            });
        }
        else
        {
            mViewMvc.loadFragment(new OverviewFragment(mViewMvc, mAccountKey), getSupportFragmentManager(), false);
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
                    List<String> trainingKeys = new DBHandler(getApplicationContext()).getAllTrainingsKeys(mAccountKey);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFirebaseDatabaseHelper.getKeysToDownload(mAccountKey, trainingKeys).addOnSuccessListener(new OnSuccessListener<List<String>>() {
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
        new DownloadWorkHelper().startWorkIfNotExists(getApplicationContext(), mAccountKey, trainingKeys).observe(MainActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                Log.d("DownloadWorkerState", workInfo.getState().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        MenuItem checkedItem = mViewMvc.getCheckedItem();

        if((checkedItem == null || checkedItem.getItemId() != R.id.settings_nav_menu) && !mViewMvc.showNavigation())
        {
            super.onBackPressed();
        }
    }
}
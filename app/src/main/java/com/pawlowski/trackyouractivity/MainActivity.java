package com.pawlowski.trackyouractivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.history.HistoryFragment;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.overview.OverviewFragment;
import com.pawlowski.trackyouractivity.settings.ProfileSettingsFragment;
import com.pawlowski.trackyouractivity.tracking.TrackingActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    MainViewMvc mViewMvc;
    SharedPreferencesHelper mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new MainViewMvc(getLayoutInflater(), null, this);

        setContentView(mViewMvc.getRootView());

        mSharedPreferences = new SharedPreferencesHelper(getSharedPreferences(ConstAndStaticMethods.SHARED_PREFERENCES_NAME, MODE_MULTI_PROCESS));

        mViewMvc.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.overview_nav_menu)
                {
                    mViewMvc.loadFragment(new OverviewFragment(mViewMvc), getSupportFragmentManager(), false);
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
                    mViewMvc.loadFragment(new HistoryFragment(mViewMvc), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.history_nav_menu);
                }
                else if(item.getItemId() == R.id.settings_nav_menu)
                {
                    mViewMvc.loadFragment(new ProfileSettingsFragment(mViewMvc), getSupportFragmentManager(), false);
                    mViewMvc.checkItem(R.id.history_nav_menu);
                }

                    return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if((bundle != null && bundle.getBoolean("start_with_settings", false)) || !mSharedPreferences.isProfileSaved())
        {
            mViewMvc.loadFragment(new ProfileSettingsFragment(mViewMvc), getSupportFragmentManager(), false);
        }
        else
        {
            mViewMvc.loadFragment(new OverviewFragment(mViewMvc), getSupportFragmentManager(), false);
        }

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
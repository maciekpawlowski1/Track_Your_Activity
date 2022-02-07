package com.pawlowski.trackyouractivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment fragmentHolder;
    MainViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewMvc = new MainViewMvc(getLayoutInflater(), null, this);

        setContentView(viewMvc.getRootView());

        viewMvc.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.overview_nav_menu)
                {
                    viewMvc.loadFragment(new OverviewFragment(viewMvc), getSupportFragmentManager(), false);
                }
                else if (item.getItemId() == R.id.track_nav_menu)
                {

                }
                else if (item.getItemId() == R.id.history_nav_menu)
                {

                }

                    return false;
            }
        });

        viewMvc.loadFragment(new OverviewFragment(viewMvc), getSupportFragmentManager(), false);
    }

    @Override
    public void onBackPressed() {
        if(!viewMvc.showNavigation())
        {
            super.onBackPressed();
        }
    }
}
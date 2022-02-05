package com.pawlowski.trackyouractivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainViewMvc extends BaseViewMvc {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;

    public MainViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent, AppCompatActivity activity) {

        rootView = inflater.inflate(R.layout.activity_main, parent, false);

        drawerLayout= findViewById(R.id.drawer);
        //toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.nav_view);


        //activity.setSupportActionBar(toolbar);
        //toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.Open, R.string.Close);
        toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        //toggle.syncState();

        //View headerView = navigationView.getHeaderView(0);
        //helloText = headerView.findViewById(R.id.hello_text_nav_header);




    }

    public boolean showNavigation()
    {
        if(!drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void loadFragment(Fragment fragment, FragmentManager fragmentManager, boolean addToStack)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame_layout_main, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        if(addToStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        hideKeyboard();
    }

    public void setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener listener)
    {
        navigationView.setNavigationItemSelectedListener(listener);
    }


}

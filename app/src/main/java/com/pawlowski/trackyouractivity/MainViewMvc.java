package com.pawlowski.trackyouractivity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.pawlowski.trackyouractivity.base.BaseViewMvc;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainViewMvc extends BaseViewMvc {
    private final DrawerLayout mDrawerLayout;
    private final ActionBarDrawerToggle mToggle;
    private final NavigationView mNavigationView;

    public MainViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent, AppCompatActivity activity) {

        rootView = inflater.inflate(R.layout.activity_main, parent, false);

        mDrawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.nav_view);


        mToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mToggle);

        //View headerView = navigationView.getHeaderView(0);
        //helloText = headerView.findViewById(R.id.hello_text_nav_header);




    }


    public @Nullable MenuItem getCheckedItem()
    {
        return mNavigationView.getCheckedItem();
    }

    public void checkItem(@IdRes int itemId)
    {
        mNavigationView.setCheckedItem(itemId);
    }


    public boolean showNavigation()
    {
        if(!mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void hideNavigation()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    public void loadFragment(Fragment fragment, FragmentManager fragmentManager, boolean addToStack)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame_layout_main, fragment).commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if(addToStack)
        {
            fragmentTransaction.addToBackStack(null);
        }
        hideKeyboard();
    }

    public void setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener listener)
    {
        mNavigationView.setNavigationItemSelectedListener(listener);
    }


}

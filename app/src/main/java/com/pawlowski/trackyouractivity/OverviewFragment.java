package com.pawlowski.trackyouractivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class OverviewFragment extends Fragment {

    ImageButton menuButton;
    MainViewMvc mainActivity;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public OverviewFragment(MainViewMvc mainActivity) {
        // Required empty public constructor
        this.mainActivity = mainActivity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        menuButton = view.findViewById(R.id.menu_open_button_fragment_overview);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.showNavigation();
            }
        });

        return view;
    }
}
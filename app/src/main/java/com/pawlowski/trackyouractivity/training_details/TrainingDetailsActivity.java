package com.pawlowski.trackyouractivity.training_details;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.tracking.MapHelper;

public class TrainingDetailsActivity extends AppCompatActivity implements TrainingDetailsViewMvc.TrainingDetailsButtonsClickListener{

    TrainingDetailsViewMvc mViewMvc;
    MapHelper mMapHelper;
    TrainingModel mTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new TrainingDetailsViewMvc(getLayoutInflater(), null);
        setContentView(mViewMvc.getRootView());

        int trainingId = getIntent().getExtras().getInt("training_id");




        mTraining = new DBHandler(getApplicationContext()).getTraining(trainingId);
        mMapHelper = new MapHelper(mTraining.getKey(), getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_tracking);
        assert mapFragment != null;
        mapFragment.getMapAsync(mMapHelper);

        mViewMvc.bindTraining(mTraining);
        mViewMvc.registerListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
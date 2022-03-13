package com.pawlowski.trackyouractivity.training_details;

import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseActivity;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.tracking.MapHelper;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingDetailsActivity extends BaseActivity implements TrainingDetailsViewMvc.TrainingDetailsButtonsClickListener{

    TrainingDetailsViewMvc mViewMvc;
    MapHelper mMapHelper;
    TrainingModel mTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getTrainingDetailsViewMvc(null);
        setContentView(mViewMvc.getRootView());

        int trainingId = getIntent().getExtras().getInt("training_id");




        mTraining = getCompositionRoot().getDBHandler().getTraining(trainingId);
        mMapHelper = getCompositionRoot().getMapHelper(mTraining.getKey(), false, null);

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
package com.pawlowski.trackyouractivity.tracking;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.base.BaseActivity;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;
import com.pawlowski.trackyouractivity.database.DBHandler;
import com.pawlowski.trackyouractivity.database.FirebaseDatabaseHelper;
import com.pawlowski.trackyouractivity.database.SharedPreferencesHelper;
import com.pawlowski.trackyouractivity.models.LocationUpdateModel;
import com.pawlowski.trackyouractivity.models.TimeUpdateModel;
import com.pawlowski.trackyouractivity.models.TrackingStopUpdate;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.service.TrackingService;
import com.pawlowski.trackyouractivity.upload_job.WorkHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

public class TrackingActivity extends BaseActivity implements TrackingViewMvc.OnControlButtonsClickListener {


    private MapHelper mMapHelper;
    private TrackingViewMvc mTrackingViewMvc;
    private FusedLocationProviderClient mFusedLocationClient;
    private PermissionHelper mPermissionHelper;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private int mTrainingId = -1;
    private DBHandler mDbHandler;
    private double mDistance;
    private long mTime;
    private int mKcal;
    private int mTrainingType;
    private long mTrainingDate;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mAccountKey;
    private String mTrainingKey = null;
    private FirebaseAuthHelper mFirebaseAuthHelper;
    private boolean wasOnStartBefore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackingViewMvc = getCompositionRoot().getViewMvcFactory().getTrackingViewMvc(null);
        mTrackingViewMvc.registerListener(this);
        setContentView(mTrackingViewMvc.getRootView());

        mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.STOPPED);
        mFirebaseDatabaseHelper = getCompositionRoot().getFirebaseDatabaseHelper();
        mDbHandler = getCompositionRoot().getDBHandler();
        wasOnStartBefore = false;

        Bundle bundle = getIntent().getExtras();
        mTrainingType = bundle.getInt("training_type", TrainingModel.TrainingType.RUNNING.ordinal());
        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();

        mAccountKey = mFirebaseAuthHelper.getCurrentUser().getUid();

        TrainingModel current = mDbHandler.getCurrentTraining();
        if(current != null)
        {
            mTrainingId = current.getId();
            mTrainingKey = current.getKey();
            mTrainingDate = current.getDate();
            mTrainingType = current.getTrainingType();
        }



        mTrackingViewMvc.setTrainingTypeIcon(mTrainingType);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPermissionHelper = getCompositionRoot().getPermissionHelper();
        mMapHelper = getCompositionRoot().getMapHelper(mTrainingKey, true, mPermissionHelper);
        mSharedPreferencesHelper = getCompositionRoot().getSharedPreferencesHelper();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_tracking);
        assert mapFragment != null;
        mapFragment.getMapAsync(mMapHelper);

        if(!mPermissionHelper.isTrackingPermissionGranted())
        {
            mPermissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                @Override
                public void onSuccess() {
                    mMapHelper.startShowingLocation();
                    if(mSharedPreferencesHelper.isTrackingActive())
                    {
                        startServiceIfActive();
                    }

                }
            });
        }
        else
        {
            startServiceIfActive();
        }



    }




    @Override
    protected void onStart() {
        super.onStart();



        if(wasOnStartBefore)
        {
            mMapHelper.readGpxAndUpdateMap();
            startServiceIfActive();
        }

        EventBus.getDefault().register(this);

        wasOnStartBefore = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void startServiceIfActive()
    {
        if(mSharedPreferencesHelper.isTrackingActive())
        {
            float distance = mSharedPreferencesHelper.getCurrentDistance();
            long seconds = mSharedPreferencesHelper.getCurrentTime();
            mKcal = (int)mSharedPreferencesHelper.getCurrentKcal();
            mTrackingViewMvc.setTimeText(ConstAndStaticMethods.convertSecondsToTimeTest(seconds/1000));
            mTrackingViewMvc.setDistanceText(ConstAndStaticMethods.distanceMetersToKilometers(distance));
            mTrackingViewMvc.setSpeedText(0);
            mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PLAYED);
            mTime = seconds/1000;
            mDistance = distance;
            if(!isServiceRunning())
            {
                startService();
            }
        }
        else
        {
            if(mSharedPreferencesHelper.getCurrentTime() != 0)
            {
                mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
                float distance = mSharedPreferencesHelper.getCurrentDistance();
                long seconds = mSharedPreferencesHelper.getCurrentTime();
                mTime = seconds/1000;
                mDistance = distance;
                mTrackingViewMvc.setTimeText(ConstAndStaticMethods.convertSecondsToTimeTest(seconds/1000));
                mTrackingViewMvc.setDistanceText(ConstAndStaticMethods.distanceMetersToKilometers(distance));
                mTrackingViewMvc.setSpeedText(0);
            }
            else
            {
                mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.STOPPED);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdate(LocationUpdateModel locationUpdate)
    {
        mMapHelper.addLocationToMap(locationUpdate.getLocation());
        mDistance = locationUpdate.getAllDistance();

        mTrackingViewMvc.setDistanceText(ConstAndStaticMethods.distanceMetersToKilometers(mDistance));
        double speed = locationUpdate.getCurrentSpeed();
        speed*= 10;
        speed = Math.floor(speed);
        speed/= 10.0;
        mTrackingViewMvc.setSpeedText(speed);

        mKcal = (int) locationUpdate.getAllKcal();
        mTrackingViewMvc.setCaloriesText((int) locationUpdate.getAllKcal());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeUpdate(TimeUpdateModel time)
    {
        mTrackingViewMvc.setTimeText(ConstAndStaticMethods.convertSecondsToTimeTest(time.getTime()/1000));
        mTime = time.getTime()/1000;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrackingStopUpdate(TrackingStopUpdate stopUpdate)
    {
        if(!stopUpdate.isTracking())
        {
            mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
        }
    }

    private boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startService()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(TrackingActivity.this, TrackingService.class));
        }
        else
        {
            startService(new Intent(TrackingActivity.this, TrackingService.class));
        }

    }

    private void stopService()
    {
        sendBroadcast(new Intent(ConstAndStaticMethods.INTENT_FILTER_STOP_TEXT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.handleRequestResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStartPauseClick() {
        switch (mTrackingViewMvc.getCurrentState())
        {
            case PAUSED:
                if(!mPermissionHelper.isTrackingPermissionGranted())
                {
                    mPermissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                }
                else
                {
                    startService();
                    mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PLAYED);
                }
                break;
            case STOPPED:
                if(!mPermissionHelper.isTrackingPermissionGranted())
                {
                    mPermissionHelper.requestPermission(new PermissionHelper.OnPermissionReadyListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                }
                else
                {
                    mTrainingKey = mFirebaseDatabaseHelper.createNewEmptyTraining(mAccountKey);
                    mTrainingDate = System.currentTimeMillis();
                    mDbHandler.insertTraining(new TrainingModel(mTrainingKey, mTrainingDate, 0, 0, 0, false, mTrainingType), mAccountKey);
                    mTrainingId = mDbHandler.getCurrentTrainingId();
                    mMapHelper.clearMap();
                    startService();
                    mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PLAYED);
                }
                break;
            case PLAYED:
                stopService();
                //Service will send update by EventBus when stopped to change state
                //mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.PAUSED);
                break;

        }
    }

    @Override
    public void onStopClick() {
        //mFirebaseDatabaseHelper.addTraining(mAccountKey, mTrainingKey, mDistance, mKcal, mTime, mTrainingDate, mTrainingType);
        TrainingModel training = new TrainingModel(mTrainingKey, mDistance, mTime, mKcal, true, mTrainingType);
        training.setId(mTrainingId);
        mDbHandler.updateTraining(training);
        mTrackingViewMvc.changeButtonsState(TrackingViewMvc.ControllerButtonsState.STOPPED);
        mSharedPreferencesHelper.resetCurrentTraining();

        new WorkHelper().startWorkIfNotExists(getApplicationContext(),mAccountKey).observe(TrackingActivity.this, new Observer<>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                Log.d("info", workInfo.getState().toString());
            }
        });
        //mFirebaseDatabaseHelper.uploadFile(mAccountKey, mTrainingKey, new File(getFilesDir().getAbsolutePath() + File.separator, mTrainingId+".gpx"));
    }

    @Override
    public void onBackClick() {
        onBackPressed();
    }

    public static void launch(Context context, int trainingType)
    {
        Intent i = new Intent(context, TrackingActivity.class);
        i.putExtra("training_type", trainingType);
        context.startActivity(i);
    }
}
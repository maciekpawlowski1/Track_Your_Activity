package com.pawlowski.trackyouractivity.tracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;
import com.pawlowski.trackyouractivity.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TrackingViewMvc extends BaseObservableViewMvc<TrackingViewMvc.OnControlButtonsClickListener> {

    private final FloatingActionButton mStartPauseButton;
    private final FloatingActionButton mStopButton;
    private final TextView mTimeText;
    private final TextView mDistanceText;
    private final TextView mCaloriesText;
    private final TextView mSpeedText;
    private ControllerButtonsState mCurrentState;

    public TrackingViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent, AppCompatActivity activity) {

        rootView = inflater.inflate(R.layout.activity_tracking, parent, false);
        mStartPauseButton = findViewById(R.id.start_pause_button_tracking);
        mStopButton = findViewById(R.id.stop_button_tracking);
        mTimeText = findViewById(R.id.time_text_tracking);
        mDistanceText = findViewById(R.id.kilometers_text_kilometers_panel);
        mCaloriesText = findViewById(R.id.kcal_text_kcal_panel);
        mSpeedText = findViewById(R.id.speed_text_speed_panel);

        mStartPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichListener.START_PAUSE_LISTENER.ordinal());
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichListener.STOP_LISTENER.ordinal());
            }
        });
    }

    public void changeButtonsState(ControllerButtonsState state)
    {
        mCurrentState = state;
        if(state == ControllerButtonsState.STOPPED)
        {
            mStopButton.setVisibility(View.GONE);
            mStartPauseButton.setImageResource(R.drawable.play_icon);
        }
        else if(state == ControllerButtonsState.PLAYED)
        {
            mStopButton.setVisibility(View.GONE);
            mStartPauseButton.setImageResource(R.drawable.pause_icon);
        }
        else if(state == ControllerButtonsState.PAUSED)
        {
            mStopButton.setVisibility(View.VISIBLE);
            mStartPauseButton.setImageResource(R.drawable.play_icon);
        }
    }

    public void setTimeText(String timeText)
    {
        this.mTimeText.setText(timeText);
    }

    public void setSpeedText(double speedText)
    {
        this.mSpeedText.setText(speedText+"");
    }

    public void setCaloriesText(int caloriesText)
    {
        this.mCaloriesText.setText(caloriesText+"");
    }

    public void setDistanceText(double distanceText)
    {
        this.mDistanceText.setText(distanceText+"");
    }

    public ControllerButtonsState getCurrentState() {
        return mCurrentState;
    }

    @Override
    protected void notifyListeners(@Nullable Integer which) {
        if(which == null)
            return;
        if(which == WhichListener.START_PAUSE_LISTENER.ordinal())
        {
            for(OnControlButtonsClickListener l:listeners)
            {
                l.onStartPauseClick();
            }
        }
        else if(which == WhichListener.STOP_LISTENER.ordinal())
        {
            for(OnControlButtonsClickListener l:listeners)
            {
                l.onStopClick();
            }
        }
    }

    interface OnControlButtonsClickListener
    {
        void onStartPauseClick();
        void onStopClick();
    }

    private enum WhichListener
    {
        START_PAUSE_LISTENER,
        STOP_LISTENER
    }

    public enum ControllerButtonsState
    {
        STOPPED,
        PLAYED,
        PAUSED
    }

}

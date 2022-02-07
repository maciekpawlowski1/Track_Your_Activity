package com.pawlowski.trackyouractivity.tracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;
import com.pawlowski.trackyouractivity.base.BaseViewMvc;
import com.pawlowski.trackyouractivity.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TrackingViewMvc extends BaseObservableViewMvc<TrackingViewMvc.OnControlButtonsClickListener> {

    FloatingActionButton startPauseButton;
    FloatingActionButton stopButton;
    TextView timeText;
    TextView distanceText;
    TextView caloriesText;
    TextView speedText;

    public TrackingViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent, AppCompatActivity activity) {

        rootView = inflater.inflate(R.layout.activity_tracking, parent, false);
        startPauseButton = findViewById(R.id.start_pause_button_tracking);
        stopButton = findViewById(R.id.stop_button_tracking);
        timeText = findViewById(R.id.time_text_tracking);
        distanceText = findViewById(R.id.kilometers_text_kilometers_panel);
        caloriesText = findViewById(R.id.kcal_text_kcal_panel);
        speedText = findViewById(R.id.speed_text_speed_panel);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichListener.START_PAUSE_LISTENER.ordinal());
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichListener.STOP_LISTENER.ordinal());
            }
        });
    }

    public void changeButtonsState(ControllerButtonsState state)
    {
        if(state == ControllerButtonsState.STOPPED)
        {
            stopButton.setVisibility(View.GONE);
            startPauseButton.setImageResource(R.drawable.play_icon);
        }
        else if(state == ControllerButtonsState.PLAYED)
        {
            stopButton.setVisibility(View.GONE);
            startPauseButton.setImageResource(R.drawable.pause_icon);
        }
        else if(state == ControllerButtonsState.PAUSED)
        {
            stopButton.setVisibility(View.VISIBLE);
            startPauseButton.setImageResource(R.drawable.play_icon);
        }
    }

    public void setTimeText(String timeText)
    {
        this.timeText.setText(timeText);
    }

    public void setSpeedText(double speedText)
    {
        this.speedText.setText(speedText+"");
    }

    public void setCaloriesText(int caloriesText)
    {
        this.caloriesText.setText(caloriesText+"");
    }

    public void setDistanceText(double distanceText)
    {
        this.distanceText.setText(distanceText+"");
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

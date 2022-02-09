package com.pawlowski.trackyouractivity.overview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;
import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OverviewViewMvc extends BaseObservableViewMvc<OverviewViewMvc.OverviewButtonsListener> {
    private final ImageButton mMenuButton;
    private final RecyclerView mHistoryRecycler;
    private final TextView mCurrentTrainingDistanceText;
    private final TextView mCurrentTrainingTimeText;
    private final ImageView mCurrentTrainingTypeImage;
    private final View mCurrentTrainingInclude;
    private final View mStartTrainingInclude;
    private final TextView mStartTrainingText;
    private final ImageButton mRunningStartButton;
    private final ImageButton mCyclingStartButton;
    private final ImageButton mRollerSkatingStartButton;
    private final ImageButton mNordicWalkingStartButton;
    //private final ImageButton moreStartButton;


    public OverviewViewMvc(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        mMenuButton = findViewById(R.id.menu_open_button_fragment_overview);
        mHistoryRecycler = findViewById(R.id.recycler_history_panel);
        mCurrentTrainingDistanceText = findViewById(R.id.distance_current_training);
        mCurrentTrainingTimeText =findViewById(R.id.time_current_training);
        mCurrentTrainingInclude = findViewById(R.id.current_activity_include);
        mStartTrainingInclude = findViewById(R.id.training_choose_include);
        mStartTrainingText = findViewById(R.id.start_training_text_fragment_overview);
        mRunningStartButton = findViewById(R.id.running_choose_panel);
        mCyclingStartButton = findViewById(R.id.cycling_choose_panel);
        mRollerSkatingStartButton = findViewById(R.id.roller_skating_choose_panel);
        mNordicWalkingStartButton = findViewById(R.id.nordic_walking_choose_panel);
        mCurrentTrainingTypeImage = findViewById(R.id.current_training_type_image_current_training);

        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.MENU_BUTTON.ordinal());
            }
        });

        mCurrentTrainingInclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.CURRENT_TRAINING_CARD.ordinal());
            }
        });

        mRunningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.RUNNING_BUTTON.ordinal());
            }
        });

        mCyclingStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.CYCLING_BUTTON.ordinal());
            }
        });

        mNordicWalkingStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.NORDIC_WALKING_BUTTON.ordinal());
            }
        });

        mRollerSkatingStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(OverviewWhichButton.ROLLER_SKATING_BUTTON.ordinal());
            }
        });



    }

    public void setRecyclerAdapter(HistoryAdapter adapter)
    {
        mHistoryRecycler.setAdapter(adapter);
    }

    public void setCurrentTrainingTime(String time)
    {
        mCurrentTrainingTimeText.setText(time);
    }

    public void setCurrentTrainingDistance(String distance)
    {
        mCurrentTrainingDistanceText.setText(distance);
    }

    public void setCurrentTrainingTypeImage(int trainingType)
    {
        mCurrentTrainingTypeImage.setImageResource(Const.getImageResourceOfTrainingType(trainingType, false));
    }

    @Override
    protected void notifyListeners(@Nullable Integer which) {
        if(which == null)
            return;
        if(which == OverviewWhichButton.MENU_BUTTON.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onMenuButtonClick();
            }
        }
        else if (which == OverviewWhichButton.CURRENT_TRAINING_CARD.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onCurrentTrainingCardClick();
            }
        }
        else if (which == OverviewWhichButton.RUNNING_BUTTON.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onTrainingStartClick(TrainingModel.TrainingType.RUNNING);
            }
        }
        else if (which == OverviewWhichButton.CYCLING_BUTTON.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onTrainingStartClick(TrainingModel.TrainingType.CYCLING);
            }
        }
        else if (which == OverviewWhichButton.NORDIC_WALKING_BUTTON.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onTrainingStartClick(TrainingModel.TrainingType.NORDIC_WALKING);
            }
        }
        else if (which == OverviewWhichButton.ROLLER_SKATING_BUTTON.ordinal())
        {
            for(OverviewButtonsListener l:listeners)
            {
                l.onTrainingStartClick(TrainingModel.TrainingType.ROLLER_SKATING);
            }
        }
    }

    public void showCurrentActivityPanel() {
        mCurrentTrainingInclude.setVisibility(View.VISIBLE);
        mStartTrainingInclude.setVisibility(View.GONE);
        mStartTrainingText.setVisibility(View.GONE);
    }

    public void hideCurrentActivityAndShowStartActivityPanel() {
        mCurrentTrainingInclude.setVisibility(View.GONE);
        mStartTrainingInclude.setVisibility(View.VISIBLE);
        mStartTrainingText.setVisibility(View.VISIBLE);
    }

    interface OverviewButtonsListener
    {
        void onMenuButtonClick();
        void onCurrentTrainingCardClick();
        void onTrainingStartClick(TrainingModel.TrainingType trainingType);
    }

    enum OverviewWhichButton
    {
        MENU_BUTTON,
        CURRENT_TRAINING_CARD,
        RUNNING_BUTTON,
        CYCLING_BUTTON,
        NORDIC_WALKING_BUTTON,
        ROLLER_SKATING_BUTTON
    }
}

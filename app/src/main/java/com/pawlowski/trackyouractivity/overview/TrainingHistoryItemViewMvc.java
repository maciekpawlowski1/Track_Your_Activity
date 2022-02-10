package com.pawlowski.trackyouractivity.overview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;
import com.pawlowski.trackyouractivity.consts.Const;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.util.Date;

import androidx.annotation.Nullable;

public class TrainingHistoryItemViewMvc extends BaseObservableViewMvc<TrainingHistoryItemViewMvc.TrainingCardButtonsClickListener>
{

    private final TextView distanceText;
    private final TextView speedText;
    private final TextView kcalText;
    private final TextView dateText;
    private final TextView timeText;
    private final ImageButton openButton;
    private final ImageView typeOfTrainingImage;

    public TrainingHistoryItemViewMvc(ViewGroup parent) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_card, parent, false);
        distanceText = findViewById(R.id.distance_training_card);
        speedText = findViewById(R.id.speed_training_card);
        kcalText = findViewById(R.id.kcal_training_card);
        dateText = findViewById(R.id.date_training_card);
        timeText = findViewById(R.id.time_training_card);
        openButton = findViewById(R.id.open_button_training_card);
        typeOfTrainingImage = findViewById(R.id.training_type_image_training_card);
    }

    public void bindTraining(TrainingModel training)
    {
        String distance = Const.distanceMetersToKilometers(training.getDistance()) + " km";
        distanceText.setText(distance);
        Date date = new Date(training.getDate());
        String dateS = date.getDate() + "." + (date.getMonth()+1<10?"0"+(date.getMonth()+1):date.getMonth()+1) + "." + "20" + (date.getYear()+"").substring(1) + " " + (date.getHours()<10?"0"+date.getHours():date.getHours())+":"+(date.getMinutes()<10?"0"+date.getMinutes():date.getMinutes());
        dateText.setText(dateS);
        timeText.setText(Const.convertSecondsToTimeTest(training.getTime()));
        String kcal = training.getKcal()+" kcal";
        kcalText.setText(kcal);
        double speed = training.getDistance()/(training.getTime())/(1000./3600.);
        String speedS = Const.cutDouble(speed) + " km/h";
        speedText.setText(speedS);
        int imageResource = Const.getImageResourceOfTrainingType(training.getTrainingType(), false);
        typeOfTrainingImage.setImageResource(imageResource);

    }

    @Override
    protected void notifyListeners(@Nullable Integer which) {
        for(TrainingCardButtonsClickListener l:listeners)
        {
            l.onCardClick();
        }
    }

    interface TrainingCardButtonsClickListener
    {
        void onCardClick();
    }
}

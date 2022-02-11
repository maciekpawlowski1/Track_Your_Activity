package com.pawlowski.trackyouractivity.training_details;

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

import org.w3c.dom.Text;

import java.util.Date;

import androidx.annotation.Nullable;

public class TrainingDetailsViewMvc extends BaseObservableViewMvc<TrainingDetailsViewMvc.TrainingDetailsButtonsClickListener> {

    private final TextView mTimeText;
    private final TextView mDistanceText;
    private final TextView mCaloriesText;
    private final TextView mSpeedText;
    private final TextView mDateText;
    private final ImageButton mBackButton;
    private final ImageView mTrainingTypeImage;

    public TrainingDetailsViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent)
    {
        rootView = inflater.inflate(R.layout.activity_training_details, parent, false);

        mTimeText = findViewById(R.id.time_text_tracking);
        mDistanceText = findViewById(R.id.kilometers_text_kilometers_panel);
        mCaloriesText = findViewById(R.id.kcal_text_kcal_panel);
        mSpeedText = findViewById(R.id.speed_text_speed_panel);
        mBackButton = findViewById(R.id.back_button_back_panel);
        mTrainingTypeImage = findViewById(R.id.type_of_activity_image_type_panel);
        mDateText = findViewById(R.id.date_text_date_panel);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(null);
            }
        });
    }

    public void bindTraining(TrainingModel training)
    {
        mTrainingTypeImage.setImageResource(Const.getImageResourceOfTrainingType(training.getTrainingType(), true));
        mDistanceText.setText((Const.distanceMetersToKilometers(training.getDistance())+""));
        mTimeText.setText((Const.convertSecondsToTimeTest(training.getTime())));
        mCaloriesText.setText(training.getKcal()+"");
        double speed = training.getDistance()/(training.getTime())/(1000./3600.);
        String speedS = Const.cutDouble(speed);
        mSpeedText.setText(speedS);
        Date date = new Date(training.getDate());
        String dateS = date.getDate() + "." + (date.getMonth()+1<10?"0"+(date.getMonth()+1):date.getMonth()+1) + "." + "20" + (date.getYear()+"").substring(1) + " " + (date.getHours()<10?"0"+date.getHours():date.getHours())+":"+(date.getMinutes()<10?"0"+date.getMinutes():date.getMinutes());
        mDateText.setText(dateS);
    }

    @Override
    protected void notifyListeners(@Nullable Integer which) {
        for(TrainingDetailsButtonsClickListener l:listeners)
        {
            l.onBackClick();
        }
    }

    interface TrainingDetailsButtonsClickListener
    {
        void onBackClick();
    }
}

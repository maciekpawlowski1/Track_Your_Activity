package com.pawlowski.trackyouractivity.composition;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.MainViewMvc;
import com.pawlowski.trackyouractivity.account.sign_in.SignInViewMvc;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordViewMvc;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpViewMvc;
import com.pawlowski.trackyouractivity.history.HistoryViewMvc;
import com.pawlowski.trackyouractivity.overview.TrainingHistoryItemViewMvc;
import com.pawlowski.trackyouractivity.settings.ProfileSettingsViewMvc;
import com.pawlowski.trackyouractivity.tracking.TrackingViewMvc;
import com.pawlowski.trackyouractivity.training_details.TrainingDetailsViewMvc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewMvcFactory {

    private final LayoutInflater layoutInflater;

    ViewMvcFactory(@NonNull LayoutInflater layoutInflater)
    {
        this.layoutInflater = layoutInflater;
    }

    public SignInViewMvc getSignInViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new SignInViewMvc(layoutInflater, viewGroup);
    }

    public SignInWithPasswordViewMvc getSignInWithPasswordViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new SignInWithPasswordViewMvc(layoutInflater, viewGroup);
    }

    public SignUpViewMvc getSignUpViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new SignUpViewMvc(layoutInflater, viewGroup);
    }

    public HistoryViewMvc getHistoryViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new HistoryViewMvc(layoutInflater, viewGroup);
    }

    public TrainingHistoryItemViewMvc getTrainingHistoryItemViewMvc(@NonNull ViewGroup viewGroup)
    {
        return new TrainingHistoryItemViewMvc(viewGroup);
    }

    public ProfileSettingsViewMvc getProfileSettingsViewMvc(@Nullable ViewGroup viewGroup, @NonNull MainViewMvc mainViewMvc)
    {
        return new ProfileSettingsViewMvc(layoutInflater, viewGroup, mainViewMvc);
    }

    public TrackingViewMvc getTrackingViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new TrackingViewMvc(layoutInflater, viewGroup);
    }

    public TrainingDetailsViewMvc getTrainingDetailsViewMvc(@Nullable ViewGroup viewGroup)
    {
        return new TrainingDetailsViewMvc(layoutInflater, viewGroup);
    }

    public MainViewMvc getMainViewMvc(@Nullable ViewGroup viewGroup, @NonNull AppCompatActivity activity)
    {
        return new MainViewMvc(layoutInflater, viewGroup, activity);
    }
}

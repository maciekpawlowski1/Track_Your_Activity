package com.pawlowski.trackyouractivity.overview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.training_details.TrainingDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<TrainingModel> mTrainings = new ArrayList<>();
    Context mContext;
    public HistoryAdapter(Context appContext) {
        mContext = appContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new TrainingHistoryItemViewMvc(parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainingModel currentTraining = mTrainings.get(position);
        holder.viewMvc.bindTraining(currentTraining);
        holder.viewMvc.clearListeners();
        holder.viewMvc.registerListener(new TrainingHistoryItemViewMvc.TrainingCardButtonsClickListener() {
            @Override
            public void onCardClick() {
                TrainingDetailsActivity.launch(mContext, currentTraining.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrainings.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTrainings(List<TrainingModel> trainings)
    {
        mTrainings = trainings;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TrainingHistoryItemViewMvc viewMvc;
        public ViewHolder(@NonNull TrainingHistoryItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            this.viewMvc = viewMvc;
        }
    }
}

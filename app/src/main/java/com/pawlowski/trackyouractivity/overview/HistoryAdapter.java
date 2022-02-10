package com.pawlowski.trackyouractivity.overview;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ViewGroup;

import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<TrainingModel> mTrainings = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new TrainingHistoryItemViewMvc(parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainingModel currentTraining = mTrainings.get(position);
        holder.viewMvc.bindTraining(currentTraining);
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

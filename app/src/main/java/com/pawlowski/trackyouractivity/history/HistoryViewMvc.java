package com.pawlowski.trackyouractivity.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;
import com.pawlowski.trackyouractivity.overview.HistoryAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewMvc extends BaseObservableViewMvc<HistoryViewMvc.HistoryButtonsClickListener> {

    private final RecyclerView mHistoryRecycler;
    private final ImageButton mMenuButton;

    public HistoryViewMvc(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mHistoryRecycler = findViewById(R.id.recycler_history_panel);
        mMenuButton = findViewById(R.id.menu_open_button_fragment_history);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(null);
            }
        });
    }

    public void setRecyclerAdapter(HistoryAdapter adapter)
    {
        mHistoryRecycler.setAdapter(adapter);
    }


    @Override
    protected void notifyListeners(@Nullable Integer which) {
        for(HistoryButtonsClickListener l:listeners)
        {
            l.onMenuButtonClick();
        }
    }

    interface HistoryButtonsClickListener
    {
        void onMenuButtonClick();
    }
}

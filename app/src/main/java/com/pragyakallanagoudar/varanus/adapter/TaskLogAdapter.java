package com.pragyakallanagoudar.varanus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// 4/12: May not be needed.

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
public class TaskLogAdapter extends FirestoreAdapter<TaskLogAdapter.ViewHolder> {

    public TaskLogAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_animals_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(TaskLog.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(TaskLog tasklog) {}
    }

}
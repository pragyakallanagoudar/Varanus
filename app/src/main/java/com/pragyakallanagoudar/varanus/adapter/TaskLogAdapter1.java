/**
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

public class TaskLogAdapter1 extends FirestoreAdapter<TaskLogAdapter1.ViewHolder> {

    public String taskType;
    public TaskLogAdapter1(Query query, String taskType)
    {
        super(query);
        this.taskType = taskType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_animals_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        switch(taskType)
        {
            case "Feed":
                holder.bind(getSnapshot(position).toObject(FeedLog.class));
                break;
            case "Clean":
                holder.bind(getSnapshot(position).toObject(TaskLog.class));
                break;
            case "Behavior":
                holder.bind(getSnapshot(position).toObject(BehaviorLog.class));
                break;
            case "Exercise":
                holder.bind(getSnapshot(position).toObject(ExerciseLog.class));
                break;
            default:
                holder.bind(getSnapshot(position).toObject(TaskLog.class));
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(TaskLog tasklog) {}
    }

}
 */
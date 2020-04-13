package com.pragyakallanagoudar.varanus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 /* RecyclerView adapter for a list of Tasks.
  */

 // 4/12: May not be needed.

public class TasksAdapter extends FirestoreAdapter<TasksAdapter.ViewHolder> {

    public interface OnTasksSelectedListener
    {
        void onTasksSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private OnTasksSelectedListener mListener;  // instance of above interface

    // constructor
    public TasksAdapter(Query query, OnTasksSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityTypeView;
        TextView taskTypeView;
        TextView frequencyView;

        public ViewHolder(View itemView) {
            super(itemView);
            activityTypeView = itemView.findViewById(R.id.text_activity_type);
            taskTypeView = itemView.findViewById(R.id.text_task_type);
            frequencyView = itemView.findViewById(R.id.text_task_frequency);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnTasksSelectedListener listener) {

            Task Tasks = snapshot.toObject(Task.class);

            activityTypeView.setText(Tasks.getActivityType());
            taskTypeView.setText(Tasks.getTaskType());
            frequencyView.setText(Tasks.getFrequency());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onTasksSelected(snapshot);
                    }
                }
            });
        }

    }
}


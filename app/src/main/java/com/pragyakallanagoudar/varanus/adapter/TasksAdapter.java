package com.pragyakallanagoudar.varanus.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 /* RecyclerView adapter for a list of Tasks.
  */

public class TasksAdapter extends FirestoreAdapter<TasksAdapter.ViewHolder> {

    public interface OnCheckBoxSelectedListener
    {
        void onCheckBoxSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private OnCheckBoxSelectedListener mListener;  // instance of above interface

    // constructor
    public TasksAdapter(Query query, OnCheckBoxSelectedListener listener)
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
        TextView speciesView;
        TextView descriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            speciesView = itemView.findViewById(R.id.textView5);
            descriptionView = itemView.findViewById(R.id.textView7);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCheckBoxSelectedListener listener) {

            Task Tasks = snapshot.toObject(Task.class);

            speciesView.setText(Tasks.getSpecies());
            descriptionView.setText(Tasks.getDescription());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCheckBoxSelected(snapshot);
                    }
                }
            });
        }

    }
}


package com.pragyakallanagoudar.varanus.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReformedTaskAdapter extends FirestoreAdapter<ReformedTaskAdapter.ViewHolder>
{
    public static final String LOG_TAG = ReformedTaskAdapter.class.getSimpleName(); // log tag for debugging purposes

    // the interface that will connect this to Tasks
    public interface OnRefTasksSelectedListener
    {
        void onRefTasksSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private ReformedTaskAdapter.OnRefTasksSelectedListener mListener;  // instance of above interface

    // constructor
    public ReformedTaskAdapter(Query query, OnRefTasksSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ReformedTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ReformedTaskAdapter.ViewHolder(inflater.inflate(R.layout.item_task_reformed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReformedTaskAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.task_check_box);
            // DEBUG: Log.e(LOG_TAG, speciesView.toString());
        }

        public void bind(final DocumentSnapshot snapshot,
                         final ReformedTaskAdapter.OnRefTasksSelectedListener listener) {

            Task task = snapshot.toObject(Task.class);

            Log.e(LOG_TAG, task.description);


            checkBox.setText(task.description);
            //descriptionView.setText(task.getDescription());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRefTasksSelected(snapshot);
                    }
                }
            });
        }

    }


}

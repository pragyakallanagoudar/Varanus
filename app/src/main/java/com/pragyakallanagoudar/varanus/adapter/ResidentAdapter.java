package com.pragyakallanagoudar.varanus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Resident;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResidentAdapter extends FirestoreAdapter<ResidentAdapter.ViewHolder> {

    public interface OnResidentSelectedListener
    {
        void OnResidentSelected(DocumentSnapshot snapshot);
    }

    private OnResidentSelectedListener mListener;  // instance of above interface

    public ResidentAdapter(Query query, OnResidentSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_animal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskCountView; // displays # of tasks
        TextView animalView; // name of the animal
        ImageView animalImageView; // picture of animal
        ImageView animalStatusView; // status of animal: tasks completed/not?

        public ViewHolder(View itemView) {
            super(itemView);
            taskCountView = itemView.findViewById(R.id.task_count);
            animalView = itemView.findViewById(R.id.animal_name);
            animalImageView = itemView.findViewById(R.id.animal_image);
            animalStatusView = itemView.findViewById(R.id.animal_status);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnResidentSelectedListener listener) {

            // Saving the data to local variables
            Resident resident = snapshot.toObject(Resident.class);
            animalView.setText(resident.getName());
            animalStatusView.setBackgroundResource(R.drawable.ic_warning);
            taskCountView.setText(TasksAdapter.numTasks + " tasks remaining");

            // Third-party library to show the photo attached to a certain URL
            Glide.with(animalImageView.getContext())
                    .load(resident.getPhoto())
                    .into(animalImageView);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.OnResidentSelected(snapshot);
                    }
                }
            });
        }

    }
}


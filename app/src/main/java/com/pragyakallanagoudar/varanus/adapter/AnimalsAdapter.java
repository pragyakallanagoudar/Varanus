package com.pragyakallanagoudar.varanus.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* RecyclerView adapter for a list of Tasks.
 */

public class AnimalsAdapter extends FirestoreAdapter<AnimalsAdapter.ViewHolder> {

    public interface OnCheckBoxSelectedListener
    {
        void onCheckBoxSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private OnCheckBoxSelectedListener mListener;  // instance of above interface

    // constructor
    public AnimalsAdapter(Query query, OnCheckBoxSelectedListener listener)
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView enclosureView;
        TextView animalView;
        ImageView animalImageView;
        ImageView animalStatusView;

        public ViewHolder(View itemView) {
            super(itemView);
            enclosureView = itemView.findViewById(R.id.enclosure_name);
            animalView = itemView.findViewById(R.id.animal_name);
            animalImageView = itemView.findViewById(R.id.animal_image);
            animalStatusView = itemView.findViewById(R.id.animal_status);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCheckBoxSelectedListener listener) {

            Task Tasks = snapshot.toObject(Task.class);
            Glide.with(animalImageView.getContext())
                    .load(Tasks.getPhoto())
                    .into(animalImageView);
            enclosureView.setText(Tasks.getEnclosure());
            animalView.setText(Tasks.getSpecies());
            animalStatusView.setBackgroundResource(R.drawable.ic_warning);

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


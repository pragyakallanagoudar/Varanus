package com.pragyakallanagoudar.varanus.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Resident;
import com.pragyakallanagoudar.varanus.utilities.GlideApp;

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
        TextView enclosureView; // displays enclosure
        TextView animalView; // name of the animal
        ImageView animalImageView; // picture of animal
        // TODO: figure out how to display how many more tasks are active (likely with a field in the document)
        String TAG = ViewHolder.class.getSimpleName();

        public ViewHolder(View itemView) {
            super(itemView);
            enclosureView = itemView.findViewById(R.id.enclosure);
            animalView = itemView.findViewById(R.id.animal_name);
            animalImageView = itemView.findViewById(R.id.animal_image);
           // animalStatusView = itemView.findViewById(R.id.animal_status);
            Log.e(TAG, "making the viewholder");
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnResidentSelectedListener listener) {

            // Saving the data to local variables
            Resident resident = snapshot.toObject(Resident.class);
            Log.e(TAG, "inside the bind method");
            enclosureView.setText(resident.getSpecies() + ", " + resident.getEnclosure());

            animalView.setText(resident.getName());

            StorageReference storageReference;

            // if the photo field is empty or null, just display the Varanus logo
            if (resident.getPhoto() != null && !resident.getPhoto().equals(""))
                storageReference = FirebaseStorage.getInstance().getReference().child(resident.getPhoto());
            else
                storageReference = FirebaseStorage.getInstance().getReference().child("ic_varanus_logo2.png");

            // Load the image using Glide
            GlideApp.with(animalImageView.getContext() /* context */)
                    .load(storageReference)
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


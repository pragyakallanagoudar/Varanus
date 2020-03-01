package com.pragyakallanagoudar.varanus.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Species;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpeciesHolderAdapter extends FirestoreAdapter<SpeciesHolderAdapter.ViewHolder>
{
    public static final String LOG_TAG = SpeciesHolderAdapter.class.getSimpleName(); // log tag for debugging purposes

    // the interface that will connect this to Tasks
    public interface OnSpeciesSelectedListener
    {
        void onSpeciesSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private SpeciesHolderAdapter.OnSpeciesSelectedListener mListener;  // instance of above interface

    // constructor
    public SpeciesHolderAdapter(Query query, OnSpeciesSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.species_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpeciesHolderAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements ReformedTaskAdapter.OnRefTasksSelectedListener  {
        TextView speciesView;

        // for initialization of nested recycler view
        RecyclerView taskRecycler;
        ReformedTaskAdapter taskAdapter;
        CollectionReference tasks = FirebaseFirestore.getInstance().collection("Tasks");
        Query taskQuery = null;

        public ViewHolder(View itemView) {
            super(itemView);
            speciesView = itemView.findViewById(R.id.title);
            taskRecycler = itemView.findViewById(R.id.check_boxes);
            // DEBUG: Log.e(LOG_TAG, speciesView.toString());
        }

        public void bind(final DocumentSnapshot snapshot,
                         final SpeciesHolderAdapter.OnSpeciesSelectedListener listener) {

            Species species = snapshot.toObject(Species.class);

            // set the species header text to the name of the species
            speciesView.setText(species.name);

            // get the tasks for this species and store it in the taskQuery
            taskQuery = tasks.whereEqualTo("species", species.name);

            Log.e(LOG_TAG, taskQuery.toString());

            initRecyclerView();

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onSpeciesSelected(snapshot);
                    }
                }
            });
        }

        private void initRecyclerView()
        {
            if (taskQuery == null)
            {
                Log.e(LOG_TAG, "No query, not initializing RecyclerView");
            }

            // over here, continue building the nested recycler view

            taskAdapter = new ReformedTaskAdapter(taskQuery, this) {
                @Override
                protected void onDataChanged() {
                    // Show/hide content if the query returns empty.

                    taskRecycler.setVisibility(View.VISIBLE);

                }

                //@Override
                protected void onError(FirebaseFirestoreException e) {
                    // Show a snackbar on errors
                    Snackbar.make(itemView.findViewById(android.R.id.content),
                            "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
                }
            }; // REFORMED

            taskRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            taskRecycler.setAdapter(taskAdapter);
        }

        public void onRefTasksSelected(DocumentSnapshot tasks) {}

    }
}

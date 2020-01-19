package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.pragyakallanagoudar.varanus.adapter.TasksAdapter;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.util.Log;
import android.view.View;


public class Tasks extends AppCompatActivity implements
        View.OnClickListener,
        TasksAdapter.OnTasksSelectedListener{

    private static final String TAG = "MainActivity";
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RecyclerView mTasksRecycler;
    private TasksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        mTasksRecycler = findViewById(R.id.recycler_tasks);

        initFirestore();
        initRecyclerView();
    }
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("Tasks")
                .limit(3);
    }

    /*public void openTask(View view)
    {
        Intent intent = new Intent(this, AnimalsPage.class);
        startActivity(intent);
    }*/

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new TasksAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.

                    mTasksRecycler.setVisibility(View.VISIBLE);

            }

            //@Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mTasksRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTasksRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*// Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Apply filters
        onFilter(mViewModel.getFilters());*/

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    public void onClick (View view)
    {

    }

    public void onTasksSelected(DocumentSnapshot tasks)
    {

    }

}

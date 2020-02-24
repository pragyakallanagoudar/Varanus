package com.pragyakallanagoudar.varanus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.pragyakallanagoudar.varanus.adapter.AnimalsAdapter;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.util.Log;
import android.view.View;



public class AnimalsPage extends AppCompatActivity implements
        View.OnClickListener,
        AnimalsAdapter.OnCheckBoxSelectedListener {

    private static final String TAG = "MainActivity";
    private FirebaseFirestore mFirestore;
    private Query mQueryTasks, mQueryEnclosureLog;
    private RecyclerView mAnimalsRecycler;
    private AnimalsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_page);
        mAnimalsRecycler = findViewById(R.id.animal_recyclerview);
        initFirestore();
        initRecyclerView();

    }
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQueryTasks = mFirestore.collection("Tasks")
                .limit(50)
                .orderBy("species");
    }

    private void initRecyclerView() {

        mAdapter = new AnimalsAdapter(mQueryTasks, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.

                mAnimalsRecycler.setVisibility(View.VISIBLE);

            }

            //@Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mAnimalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAnimalsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    public void onClick (View view)
    {

    }

    @Override
    public void onCheckBoxSelected(DocumentSnapshot tasks)
    {
        Intent intent = new Intent(this, AnimalDetailGraph.class);
        intent.putExtra(TaskDetailActivity.KEY_TASK_ID, tasks.getId());
        startActivity(intent);

    }

}

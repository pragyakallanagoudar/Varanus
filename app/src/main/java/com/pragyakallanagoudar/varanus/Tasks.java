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
import android.widget.CheckBox;


public class Tasks extends AppCompatActivity implements
        View.OnClickListener,
        TasksAdapter.OnCheckBoxSelectedListener {

    private static final String TAG = "MainActivity";
    private FirebaseFirestore mFirestore;
    private Query mQueryTasks, mQueryEnclosureLog;
    private RecyclerView mTasksRecycler;
    private TasksAdapter mAdapter;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        mTasksRecycler = findViewById(R.id.recycler_tasks);
        mCheckBox = findViewById(R.id.checkBox);

       // findViewById(R.id.checkBox).setOnClickListener(this);

        initFirestore();
        initRecyclerView();

        //findViewById(R.id.checkBox).setOnClickListener(this);

    }
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQueryTasks = mFirestore.collection("Tasks")
                .limit(50);
        mQueryEnclosureLog = mFirestore.collection("EnclosureLog");
    }

    public void onCheck(View view)
    {
        Intent intent = new Intent(this, Feed.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        if (mQueryTasks == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new TasksAdapter(mQueryTasks, this) {

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
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.KEY_TASK_ID, tasks.getId());

        startActivity(intent);

    }



    //public void onCheckBoxA

}

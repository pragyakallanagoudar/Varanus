package com.pragyakallanagoudar.varanus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.pragyakallanagoudar.varanus.model.Task;
import com.pragyakallanagoudar.varanus.model.TaskLog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;
import com.pragyakallanagoudar.varanus.adapter.TaskLogAdapter;
import com.google.firebase.firestore.Query;


import java.util.Date;


public class AnimalDetailGraph extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {


    public static final String KEY_TASK_ID = "key_task_id";
    private LineChart mChartView;

    private FirebaseFirestore mFirestore;
    private DocumentReference mTaskRef;
    private ListenerRegistration mTaskRegistration;
    private TaskLog tasklog;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_graph);
        mChartView = (LineChart) findViewById(R.id.feed_line_chart);

        String taskId = getIntent().getExtras().getString(KEY_TASK_ID);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the log
        mTaskRef = mFirestore.collection("Tasks").document(taskId);

        Query taskLogQuery = mTaskRef
                .collection("tasklog").whereEqualTo("activityType", "FEED");
        //taskLogQuery.

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View v) {

    }

    public void onSubmitClicked(View v) {

    }

    public void onCancelClicked(View view) {
        finish();
    }


    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

    }
}


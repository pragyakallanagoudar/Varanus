package com.pragyakallanagoudar.varanus;

import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


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


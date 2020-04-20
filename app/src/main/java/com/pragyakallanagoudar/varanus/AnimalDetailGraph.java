package com.pragyakallanagoudar.varanus;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AnimalDetailGraph extends Fragment implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    public static final String KEY_TASK_ID = "key_task_id";
    private LineChart mOutsideChart;

    private FirebaseFirestore mFirestore;
    private DocumentReference mTaskRef;
    private ListenerRegistration mTaskRegistration;
    private TaskLog tasklog;
    private String residentID;
    View v;

    private Query mQueryOutsideLogs;

    private String TAG = AnimalDetailGraph.class.getSimpleName();

    public AnimalDetailGraph(String residentID)
    {
        this.residentID = residentID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreateView()");
        v = inflater.inflate(R.layout.activity_animal_graph, container,false);
        mOutsideChart = v.findViewById(R.id.outside_time_chart);
        getLogs("ExerciseLog");
        mOutsideChart.invalidate();


        return v;
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        initFirestore();

    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    /**
     *
     */
    private void makeGraph (String logName, List<TaskLog> logs)
    {
        Log.e(TAG, "makeGraph()");
        List<Entry> entries = new ArrayList<Entry>();
        int counter = 1;

        switch(logName) {
            case "ExerciseLog":
                for (TaskLog log : logs)
                {
                    ExerciseLog exerciseLog = (ExerciseLog)log;
                    entries.add(new Entry(counter, exerciseLog.getOutsideTime()));
                    Log.e(TAG, "DATA HERE " + exerciseLog.getOutsideTime());
                    counter++;
                }
                break;
            default:
                break;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Time Spent Outside");
        dataSet.setColor(Color.BLACK);
        // dataSet.setValueTextColor(Color.RED);
        LineData lineData = new LineData(dataSet);
        mOutsideChart.setData(lineData);
        mOutsideChart.setDrawGridBackground(false);
        Description desc = new Description();
        desc.setText("The time spent outside everyday for the past 15 days.");
        mOutsideChart.setDescription(desc);
        mOutsideChart.setNoDataText("Varanus is unable to retrieve the OutsideLogs from the database.");
        mOutsideChart.setDrawBorders(true);
        mOutsideChart.invalidate();
    }

    /** This methods gets all the logs from a certain log that has been passed in
     *  as an argument. It then proceeds to add it to the list that has been
     *  referenced as a parameter.
     * @param logName
     * @return
     */
    private void getLogs (final String logName)
    {
        Log.e(TAG, "getLogs()");
        final List<TaskLog> logs = new ArrayList<TaskLog>();

        Query mQueryLogs = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName).orderBy("completedTime", Query.Direction.ASCENDING).limit(15);
        mQueryLogs
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.e(TAG, document.getId() + " => " + document.getData());
                                switch (logName) {
                                    case "ExerciseLog":
                                        Log.e(TAG, " bonjour + " + document.toObject(ExerciseLog.class).toString());
                                        logs.add(document.toObject(ExerciseLog.class));
                                        break;
                                    default:
                                        logs.add(document.toObject(TaskLog.class));
                                        break;
                                }
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                        makeGraph(logName, logs);
                    }
                });
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
        //finish();
    }


    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

    }
}


package com.pragyakallanagoudar.varanus;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
// import com.google.type.Date;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AnimalDetailGraph extends Fragment implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    public static final String KEY_TASK_ID = "key_task_id";
    private Spinner mProfileSelector;
    private LineChart mOutsideChart;
    private BarChart mDietChart;
    private TextView mEnclosureReport;

    private FirebaseFirestore mFirestore;
    private String residentID;
    View v;


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
        mProfileSelector = v.findViewById(R.id.select_log);
        mOutsideChart = v.findViewById(R.id.outside_time_chart);
        getLogs("ExerciseLog");
        mOutsideChart.invalidate();

        mDietChart = v.findViewById(R.id.diet_chart);
        getLogs("FeedLog");
        mDietChart.invalidate();

        mEnclosureReport = v.findViewById(R.id.enclosure_report);
        // create enclosure report here

        setAllInvisible();

        mProfileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setAllInvisible();

                switch (mProfileSelector.getSelectedItem().toString()) {
                    case "Diet":
                        mDietChart.setVisibility(View.VISIBLE);
                        break;
                    case "Exercise":
                        mOutsideChart.setVisibility(View.VISIBLE);
                        break;
                    case "Enclosure":
                        mEnclosureReport.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setAllInvisible();
            }
        });

        return v;
    }

    private void setAllInvisible () {
        mOutsideChart.setVisibility(View.INVISIBLE);
        mDietChart.setVisibility(View.INVISIBLE);
        mEnclosureReport.setVisibility(View.INVISIBLE);
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
                                    case "FeedLog":
                                        logs.add(document.toObject(FeedLog.class));
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

    private void makeGraph (String logName, List<TaskLog> logs)
    {
        Log.e(TAG, "makeGraph()");
        int counter = 1;

        switch(logName) {
            case "ExerciseLog":
                // Create the Exercise Report
                List<Entry> exerciseEntries = new ArrayList<Entry>();
                for (TaskLog log : logs)
                {
                    ExerciseLog exerciseLog = (ExerciseLog)log;
                    exerciseEntries.add(new Entry(counter, exerciseLog.getOutsideTime()));
                    Log.e(TAG, "DATA HERE " + exerciseLog.getOutsideTime());
                    counter++;
                }
                LineDataSet dataSet = new LineDataSet(exerciseEntries, "Time Spent Outside");
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
                break;

            case "FeedLog":
                Log.e(TAG, "We are here to make the FeedLog Report.");
                List<BarEntry> feedEntries = new ArrayList<BarEntry>();
                long weekBreakoff = logs.get(0).getCompletedTime() + 10 /**604800000*/;
                int cricketCount = 0;
                int ratCount = 0;
                for (int i = 0; i < logs.size(); i++)
                {
                    FeedLog feedLog = (FeedLog)logs.get(i);
                    if (feedLog.getFoodName().equalsIgnoreCase("Crickets")) {
                        cricketCount += feedLog.getFoodCount();
                        Log.e(TAG, "We have a Cricket FeedLog!");
                    }
                    else
                    {
                        ratCount += feedLog.getFoodCount();
                        Log.e(TAG, "We have a Rat FeedLog!");
                    }
                    if (i < logs.size() - 1 && logs.get(i + 1).getCompletedTime() > weekBreakoff)
                    {
                        weekBreakoff = logs.get(i + 1).getCompletedTime() + 10 /**604800000*/;
                        feedEntries.add(new BarEntry(counter, new float[] {cricketCount, ratCount}));
                        cricketCount = ratCount = 0;
                        counter++;
                        Log.e(TAG, "New Week");
                    }
                }
                // There is one odd case that hasn't been handled that will come up.

                feedEntries.add(new BarEntry(counter, new float[] {cricketCount, ratCount}));

                BarDataSet foodSet;
                if (mDietChart.getData() != null &&
                        mDietChart.getData().getDataSetCount() > 0) {
                    Log.e(TAG, "First conditional space");
                    foodSet = (BarDataSet) mDietChart.getData().getDataSetByIndex(0);
                    foodSet.setValues(feedEntries);
                    mDietChart.getData().notifyDataChanged();
                    mDietChart.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Second conditional space");
                    foodSet = new BarDataSet(feedEntries, "Diet History in Past Month");
                    Log.e(TAG, foodSet.toString());
                    foodSet.setDrawIcons(false);
                    Resources res = getResources();
                    int colors[] = {res.getColor(R.color.colorPrimary), res.getColor(R.color.colorPrimaryDark)};
                    foodSet.setColors(colors);
                    foodSet.setStackLabels(new String[]{"Crickets", "Rats"});
                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(foodSet);

                    BarData data = new BarData(dataSets);
                    data.setValueTextColor(Color.WHITE);

                    mDietChart.setData(data);
                }

                mDietChart.setFitBars(true);
                mDietChart.invalidate();

                break;
        }

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


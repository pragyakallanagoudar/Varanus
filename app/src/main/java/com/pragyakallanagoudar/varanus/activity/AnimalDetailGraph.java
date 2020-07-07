package com.pragyakallanagoudar.varanus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
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
// import com.google.type.Date;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.TaskType;
import com.pragyakallanagoudar.varanus.utilities.Utils;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * The AnimalDetailGraph activity displays insights about the selected animal:
 * diet, outside time, behavior logs, enclosure cleaning history, and the action
 * to send logs via email.
 */

public class AnimalDetailGraph extends Fragment /**implements
        /**View.OnClickListener,
        EventListener<DocumentSnapshot>*/ {

    public static final String KEY_TASK_ID = "key_task_id";

    private Spinner mProfileSelector; // which report to show
    private LineChart mOutsideChart; // line graph showing time spent outside
    private BarChart mDietChart; // stacked bar chart with diet information
    private ScrollView mBehaviorView; // textview for the...
    private TextView mBehaviorReport; // ...journal with daily logs about behavior
    private CompactCalendarView mEnclosureCalendar; // calendar showing enclosure logs
    private TextView mMonthView; // shows the current month of the calendar

    private FirebaseFirestore mFirestore; // Firestore instance
    private String residentID; // the residentID of the selected animal
    private String residentName; // the name of this animal
    View v;

    private Intent emailIntent; // the intent to start EmailSummaryActivity

    private String TAG = AnimalDetailGraph.class.getSimpleName();

    private boolean[] reportGenerated; // whether or not each report has been generated

    public AnimalDetailGraph(String residentID, String residentName)
    {
        this.residentID = residentID;
        this.residentName = residentName;
    }

    /**
     * Instantiate all of the elements on the screen. Call the appropriate method when an item from the
     * spinner is selected.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreateView()");

        // Instantiate all of the variables.

        v = inflater.inflate(R.layout.activity_animal_graph, container,false);
        mProfileSelector = v.findViewById(R.id.select_log);
        mOutsideChart = v.findViewById(R.id.outside_time_chart);
        mOutsideChart.invalidate();

        mDietChart = v.findViewById(R.id.diet_chart);
        mDietChart.invalidate();

        mEnclosureCalendar = v.findViewById(R.id.enclosure_calendar);
        mEnclosureCalendar.shouldScrollMonth(true);
        mMonthView = v.findViewById(R.id.month_view);

        mBehaviorReport = v.findViewById(R.id.general_text_view);
        mBehaviorView = v.findViewById(R.id.general_view);

        emailIntent = new Intent(getContext(), EmailSummaryActivity.class);

        setAllInvisible();

        reportGenerated = new boolean[4];

        mProfileSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // When an item from the list is selected.
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setAllInvisible();

                switch (mProfileSelector.getSelectedItem().toString()) {
                    case "Diet":
                        mDietChart.setVisibility(View.VISIBLE);
                        if (!reportGenerated[0])
                            getLogs("FeedLog");
                        break;
                    case "Exercise":
                        mOutsideChart.setVisibility(View.VISIBLE);
                        if (!reportGenerated[1])
                            getLogs("ExerciseLog");
                        break;
                    case "Enclosure":
                        mEnclosureCalendar.setVisibility(View.VISIBLE);
                        mMonthView.setVisibility(View.VISIBLE);
                        if (!reportGenerated[2])
                            getLogs("EnclosureLog");
                        break;
                    case "Behavior":
                        mBehaviorReport.setVisibility(View.VISIBLE);
                        mBehaviorView.setVisibility(View.VISIBLE);
                        if (!reportGenerated[3])
                            getLogs("BehaviorLog");
                        break;
                    case "Email Summary":
                        sendEmailSummary();
                        // summary = true;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setAllInvisible();
            }
        });

        return v;
    }

    /**
     * Makes all the view invisible. After this, can selectively show some views.
     */
    private void setAllInvisible () {
        mOutsideChart.setVisibility(View.INVISIBLE);
        mDietChart.setVisibility(View.INVISIBLE);
        mBehaviorReport.setVisibility(View.INVISIBLE);
        mBehaviorView.setVisibility(View.INVISIBLE);
        mEnclosureCalendar.setVisibility(View.INVISIBLE);
        mMonthView.setVisibility(View.INVISIBLE);
    }


    /**
     * Instantiate the Firestore database instance.
     */
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();

    }

    /** This methods gets all the logs from a certain log that has been passed in
     *  as an argument. It then proceeds to add it to the list that has been
     *  referenced as a parameter.
     * @param logName   the name of the log associated with the selected item in the list.
     * @return
     */
    private void getLogs (final String logName)
    {
        Log.e(TAG, "getLogs()");
        final ArrayList<TaskLog> logs = new ArrayList<TaskLog>();

        Query mQueryLogs = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName).orderBy("completedTime", Query.Direction.DESCENDING);
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
                                    case "EnclosureLog":
                                        logs.add(document.toObject(EnclosureLog.class));
                                        break;
                                    case "BehaviorLog":
                                        logs.add(document.toObject(TextLog.class));
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

    /**
     * Call the appropriate method based on the name of log.
     * @param logName       the name of the log
     * @param logs          the list of logs
     */
    private void makeGraph (String logName, List<TaskLog> logs)
    {
        Log.e(TAG, "makeGraph()");
        switch (logName) {
            case "ExerciseLog":
                makeExerciseReport(logs);
                break;

            case "FeedLog":
                makeDietReport(logs);
                break;

            case "EnclosureLog":
                makeEnclosureReport(logs);
                break;

            case "BehaviorLog":
                makeBehaviorReport(logs);
                break;
        }
    }

    /**
     * If "Send Email Summary" is selected, start the email activity.
     */
    private void sendEmailSummary ()
    {
        Log.e(TAG, "sendEmailSummary ()");
        emailIntent.putExtra(EmailSummaryActivity.RESIDENT_ID, residentID);
        emailIntent.putExtra(EmailSummaryActivity.RESIDENT_NAME, residentName);
        startActivity(emailIntent);
    }

    /**
     * Create the stacked bar chart based on the diet information.
     * @param logs      the list of DietLog instances
     */
    private void makeDietReport (List<TaskLog> logs)
    {
        Log.e(TAG, "makeDietReport ()");
        int counter = 1;
        if (logs.size() > 0) {
            Log.e(TAG, "We are here to make the FeedLog Report.");
            List<BarEntry> feedEntries = new ArrayList<BarEntry>();
            long weekBreakoff = logs.get(0).getCompletedTime() + 10;
            int cricketCount = 0;
            int ratCount = 0;
            for (int i = 0; i < logs.size(); i++) {
                FeedLog feedLog = (FeedLog) logs.get(i);
                if (feedLog.getFoodName().equalsIgnoreCase("Crickets")) {
                    cricketCount += feedLog.getFoodCount();
                    Log.e(TAG, "We have a Cricket FeedLog!");
                } else {
                    ratCount += feedLog.getFoodCount();
                    Log.e(TAG, "We have a Rat FeedLog!");
                }
                if (i < logs.size() - 1 && logs.get(i + 1).getCompletedTime() > weekBreakoff) {
                    weekBreakoff = logs.get(i + 1).getCompletedTime() + 10 /**604800000*/;
                    feedEntries.add(new BarEntry(counter, new float[]{cricketCount, ratCount}));
                    cricketCount = ratCount = 0;
                    counter++;
                    Log.e(TAG, "New Week");
                }
            }
            // There is one odd case that hasn't been handled that will come up.

            feedEntries.add(new BarEntry(counter, new float[]{cricketCount, ratCount}));

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
        }
        reportGenerated[0] = true;
    }

    /**
     * Create the line graph based on the exercise time information
     * @param logs      the list of ExerciseLog instances
     */
    private void makeExerciseReport (List<TaskLog> logs)
    {
        Resources resources = getResources();
        Log.e(TAG, "makeExerciseReport ()");
        int counter = 1;
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
        dataSet.setCircleColors(resources.getColor(R.color.colorAccent));
        LineData lineData = new LineData(dataSet);
        mOutsideChart.setData(lineData);
        mOutsideChart.setDrawGridBackground(false);
        Description desc = new Description();
        desc.setText("The time spent outside everyday for the past 15 days.");
        mOutsideChart.setDescription(desc);
        mOutsideChart.setNoDataText("Varanus is unable to retrieve the OutsideLogs from the database.");
        mOutsideChart.setDrawBorders(true);
        mOutsideChart.invalidate();

        reportGenerated[1] = true;
    }

    /**
     * Create the enclosure calendar based on enclosure logs.
     * @param logs      the list of EnclosureLog instances.
     */
    private void makeEnclosureReport(final List<TaskLog> logs)
    {
        Resources resources = getResources();
        mEnclosureCalendar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark));
        Log.e(TAG, "makeEnclosureReport ()");
        int[] colors = {Color.RED, Color.YELLOW, Color.GREEN};
        final String[] texts = {"Not Cleaned", "Cleaned", "Deep Cleaned"};
        mMonthView.setText(getSimpleDate(mEnclosureCalendar.getFirstDayOfCurrentMonth()));
        for (TaskLog log : logs)
        {
            EnclosureLog enclosureLog = (EnclosureLog)log;
            Event event = new Event(Color.LTGRAY, enclosureLog.getCompletedTime(), "Unidentified Event");
            if (enclosureLog.getTask() == TaskType.CLEAN)
            {
                int cleanLevel = enclosureLog.getCleanLevel();
                event = new Event(colors[cleanLevel], enclosureLog.getCompletedTime(), texts[cleanLevel]);
                mEnclosureCalendar.addEvent(event);
            }
            else
            {
                event = new Event(Color.MAGENTA, enclosureLog.getCompletedTime(), "Enriched");
            }
            mEnclosureCalendar.addEvent(event);
        }
        mEnclosureCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                final Context context = v.getContext();

                for (TaskLog log : logs)
                {
                    EnclosureLog enclosureLog = (EnclosureLog)log;
                    Date logDate = new Date(enclosureLog.getCompletedTime());
                    if (logDate.getTime() - dateClicked.getTime() < 86400000 && logDate.getTime() - dateClicked.getTime() > 0)
                    {
                        Log.e(TAG, "There is a log corresponding to this date.");
                        Log.e(TAG, logDate.getTime() - dateClicked.getTime() + "");
                        String date = Utils.getDate(dateClicked.toString());
                        if (enclosureLog.getTask() == TaskType.CLEAN)
                        {
                            String text = texts[enclosureLog.getCleanLevel()] + " on " + date + " by " + enclosureLog.getUser();
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context, "Enriched on " + date + " by " + enclosureLog.getUser(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mMonthView.setText(getSimpleDate(firstDayOfNewMonth));
            }
        });
        reportGenerated[2] = true;
    }

    /**
     * Get a simple String version of the date.
     * @param firstDayOfNewMonth        the Date instance representing the first date of this month
     * @return
     */
    public String getSimpleDate (Date firstDayOfNewMonth)
    {
        String dateStr = firstDayOfNewMonth.toString();
        String month = dateStr.substring(dateStr.indexOf(' ') + 1,
                dateStr.indexOf(' ', 4));
        String year = dateStr.substring(dateStr.lastIndexOf(' '));
        switch (month) {
            case "Jan":
                month = "January";
                break;
            case "Feb":
                month = "February";
                break;
            case "Mar":
                month = "March";
                break;
            case "Apr":
                month = "April";
                break;
            case "May":
                month = "May";
                break;
            case "Jun":
                month = "June";
                break;
            case "Jul":
                month = "July";
                break;
            case "Aug":
                month = "August";
                break;
            case "Sep":
                month = "September";
                break;
            case "Oct":
                month = "October";
                break;
            case "Nov":
                month = "November";
                break;
            case "Dec":
                month = "December";
                break;
        }
        return month + " " + year;
    }

    /**
     * Create the journal of behavior reports.
     * @param logs      the list of TextLog instances.
     */
    private void makeBehaviorReport(List<TaskLog> logs)
    {
        Log.e(TAG, "makeBehaviorReport()");
        Log.e("hello", this.getClass().getSimpleName());
        String source = new String();
        for (TaskLog log : logs)
        {
            TextLog behaviorLog = (TextLog)log;
            Date date = new Date(log.getCompletedTime());
            String dateString = Utils.getDate(date.toString());
            // make the source string in HTML to be cool
            source += "<b>" + dateString + " by " + log.getUser() + "</b><br>" + behaviorLog.getBehaviorText() + "<br><br>";
        }
        Log.e(this.getClass().getSimpleName(), source);
        mBehaviorReport.setText(Html.fromHtml(source));
        reportGenerated[3] = true;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}


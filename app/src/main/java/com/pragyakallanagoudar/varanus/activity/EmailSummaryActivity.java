package com.pragyakallanagoudar.varanus.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Utils;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RESIDENT_NAME = "resident_name";
    public static final String RESIDENT_ID = "resident_id";

    private FirebaseFirestore mFirestore;
    public String residentName, residentID;
    public List<TaskLog> feedLogs;
    public List<TaskLog> exerciseLogs;
    public List<TaskLog> enclosureLogs;
    public List<TaskLog> behaviorLogs;

    public TextView mEmailText;
    public EditText mRecipients, mStartDate, mEndDate;

    public boolean allDone;

    public static String TAG = EmailSummaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmailText = findViewById(R.id.description);

        mRecipients = findViewById(R.id.recipient_email);
        mStartDate = findViewById(R.id.start_date);
        mEndDate = findViewById(R.id.end_date);

        findViewById(R.id.send_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);

        initFirestore();

        Resources res = getResources();

        feedLogs = new ArrayList<>();
        exerciseLogs = new ArrayList<>();
        enclosureLogs = new ArrayList<>();
        behaviorLogs = new ArrayList<>();

        residentName = getIntent().getExtras().getString(RESIDENT_NAME);
        residentID = getIntent().getExtras().getString(RESIDENT_ID);

        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mEmailText.setText(String.format(res.getString(R.string.email_summary_description), user, residentName));

        getLogs("FeedLog");
        getLogs("ExerciseLog");
        getLogs("EnclosureLog");
        getLogs("TextLog");
        Log.e(TAG, "We're all done! Yay!");
        allDone = true;
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
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
        final ArrayList<TaskLog> logs = new ArrayList<TaskLog>();

        Query mQueryLogs = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName).orderBy("completedTime", Query.Direction.ASCENDING);
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
                                        exerciseLogs.add(document.toObject(ExerciseLog.class));
                                        break;
                                    case "FeedLog":
                                        feedLogs.add(document.toObject(FeedLog.class));
                                        break;
                                    case "EnclosureLog":
                                        enclosureLogs.add(document.toObject(EnclosureLog.class));
                                        break;
                                    case "TextLog":
                                        behaviorLogs.add(document.toObject(TextLog.class));
                                        break;
                                    default:
                                        logs.add(document.toObject(TaskLog.class));
                                        break;
                                }
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.send_button:
                Log.e(TAG, "send button");
                sendMail();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }

    private void sendMail ()
    {
        while (!allDone)
            Snackbar.make(findViewById(android.R.id.content), "Please wait as we load the data.", Snackbar.LENGTH_SHORT).show();

        Log.e(TAG, "sendMail()");
        // There is a strange bug here. Here's everything
        String recipientList = mRecipients.getText().toString();
        String[] recipients = recipientList.split(",");
        String subject = "Varanus: Health & Enrichment Report for " + residentName;
        String report = "";

        // it's really the date conversion that needs to work
        // and then getting all the logs, of course.

        Date startDate = new Date();
        Date endDate = new Date();
        boolean allIsWell = true;

        startDate = retrieveDate(mStartDate.getText().toString().trim(), true);
        endDate = retrieveDate(mEndDate.getText().toString().trim(), false);
        allIsWell = startDate != null && endDate != null;

        if (!allIsWell && startDate.after(endDate))
        {
            Snackbar.make(findViewById(android.R.id.content), "Start date must be before end date.", Snackbar.LENGTH_SHORT).show();
            allIsWell = false;
        }

        // Use the dates given to retrieve all of the logs.
        // Best way: iterate through all dates from start to end. Print out all logs from each day.

        if (allIsWell) {
            Log.e(TAG, "start: " + startDate.toString());
            Log.e(TAG, "end " + endDate.toString());

            String feedText = "", exerciseText = "", enclosureText = "", behaviorText = "";
            int dietPos, exercisePos, enclosurePos, behaviorPos;

            // Make the report here after Lauren responds to the email.
            // We have four lists on different time tracks. We need to write a piece of code that
            // breaks them up by day and stitches them into one journal.

            // To start, find the starting position in each list:
            dietPos = getPosition(feedLogs, startDate.getTime());
            exercisePos = getPosition(exerciseLogs, startDate.getTime());
            enclosurePos = getPosition(exerciseLogs, startDate.getTime());
            behaviorPos = getPosition(behaviorLogs, startDate.getTime());

            for (long time = startDate.getTime(); time <= endDate.getTime(); time+=86400000)
            {
                while (dateIsGood(feedLogs, dietPos, time))
                {
                    FeedLog feedLog = (FeedLog)feedLogs.get(dietPos);
                    feedText += feedLog.toString() + "\n";
                    dietPos++;
                }
                while (dateIsGood(exerciseLogs, exercisePos, time))
                {
                    ExerciseLog exerciseLog = (ExerciseLog)exerciseLogs.get(exercisePos);
                    exerciseText += exerciseLog.toString() + "\n";
                    exercisePos++;
                }
                while (dateIsGood(enclosureLogs, enclosurePos, time))
                {
                    EnclosureLog enclosureLog = (EnclosureLog)enclosureLogs.get(enclosurePos);
                    enclosureText = enclosureLog.toString() + "\n";
                    enclosurePos++;
                }
                while (dateIsGood(behaviorLogs, behaviorPos, time))
                {
                    TextLog behaviorLog = (TextLog)behaviorLogs.get(behaviorPos);
                    behaviorText = behaviorLog.toString() + "\n";
                    behaviorPos++;
                }
                if (!feedText.equals("") || !exerciseText.equals("")
                        || !enclosureText.equals("") || !behaviorText.equals("")) {
                    // add to report
                    report += Utils.getDate(new Date(time).toString()) + ":\n";
                    if (!feedText.equals("")) report += feedText;
                    if (!exerciseText.equals("")) report += exerciseText;
                    if (!enclosureText.equals("")) report += enclosureText;
                    if (!behaviorText.equals("")) report += behaviorText;
                    report += "\n";
                    feedText = exerciseText = enclosureText = behaviorText = "";
                }

            }
            Log.e(TAG, report);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, report);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // also why is this showing Google Drive...?
            // because other apps can also have the whole message thing.
            // Actually, just test it on the actual tab first.
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client to send this report."));
        }
    }

    private boolean dateIsGood (List<TaskLog> logs, int position, long time)
    {
        // long completedTime = logs.get(position).getCompletedTime();
        if (position >= logs.size()) return false;
        long completedTime = logs.get(position).getCompletedTime();
        return completedTime >= time && completedTime <= time + 86400000;
    }


    private int getPosition (List<TaskLog> logs, long time)
    {
        for (int i = 0; i < logs.size(); i++)
        {
            long completedTime = logs.get(i).getCompletedTime();
            if (completedTime >= time)
                return i;
        }
        return -1;
    }

    private Date retrieveDate (String dateStr, boolean isStart)
    {

        try {
            int month, day, year;
            Date date;
            Log.e(TAG, dateStr);

            Pattern pattern = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(dateStr);
            if (!matcher.find() || !matcher.group().equals(dateStr)) {
                throw new Exception();
            } else {
                month = Integer.parseInt(dateStr.substring(0, dateStr.indexOf('/'))) - 1;
                day = Integer.parseInt(dateStr.substring(dateStr.indexOf('/') + 1, dateStr.lastIndexOf('/')));
                year = Integer.parseInt(dateStr.substring(dateStr.lastIndexOf('/') + 1));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.YEAR, year);
                date = calendar.getTime();
                return date;
            }
        } catch (Exception e) {
            if (isStart)
                Snackbar.make(findViewById(android.R.id.content), "Invalid start date.", Snackbar.LENGTH_SHORT).show();
            else
                Snackbar.make(findViewById(android.R.id.content), "Invalid end date.", Snackbar.LENGTH_SHORT).show();
            return null;
        }
    }
}

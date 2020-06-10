package com.pragyakallanagoudar.varanus.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.grpc.ClientStreamTracer;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.log.BehaviorLog;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RESIDENT_NAME = "resident_name";
    public static final ArrayList<FeedLog> FEED_LOGS = null;
    public static final ArrayList<ExerciseLog> EXERCISE_LOGS = null;
    public static final ArrayList<EnclosureLog> ENCLOSURE_LOGS = null;
    public static final ArrayList<BehaviorLog> BEHAVIOR_LOGS = null;

    public String residentName;
    public List<TaskLog> feedTaskLogs;
    public List<FeedLog> feedLogs;
    public List<TaskLog> exerciseTaskLogs;
    public List<ExerciseLog> exerciseLogs;
    public List<TaskLog> enclosureTaskLogs;
    public List<EnclosureLog> enclosureLogs;
    public List<TaskLog> behaviorTaskLogs;
    public List<BehaviorLog> behaviorLogs;

    public TextView mEmailText;
    public EditText mRecipients, mStartDate, mEndDate;

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

        Resources res = getResources();

        residentName = getIntent().getExtras().getString(RESIDENT_NAME);

        feedTaskLogs = getIntent().getExtras().getParcelableArrayList(String.valueOf(FEED_LOGS));
        feedLogs = null;/**getIntent().getExtras().getParcelableArrayList(String.valueOf(FEED_LOGS));*/

        exerciseTaskLogs = getIntent().getExtras().getParcelableArrayList(String.valueOf(EXERCISE_LOGS));
        exerciseLogs = null;/**getIntent().getExtras().getParcelableArrayList(String.valueOf(EXERCISE_LOGS));*/

        enclosureTaskLogs = getIntent().getExtras().getParcelableArrayList(String.valueOf(ENCLOSURE_LOGS));
        enclosureLogs = null;/**getIntent().getExtras().getParcelableArrayList(String.valueOf(ENCLOSURE_LOGS));*/

        behaviorTaskLogs = getIntent().getExtras().getParcelableArrayList(String.valueOf(BEHAVIOR_LOGS));
        behaviorLogs = null;/**getIntent().getExtras().getParcelableArrayList(String.valueOf(BEHAVIOR_LOGS));*/

        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mEmailText.setText(String.format(res.getString(R.string.email_summary_description), user, residentName));

        Log.e(TAG, "the note 6/9 " + feedLogs.get(0).toString());

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
        Log.e(TAG, "sendMail()");
        // There is a strange bug here. Here's everything
        String recipientList = mRecipients.getText().toString();
        String[] recipients = recipientList.split(",");
        String subject = "Varanus: Health & Enrichment Report for " + residentName;
        String report = "";

        // it's really the date conversion that needs to work
        // and then getting all the logs, of course.

        /**
        Date startDate = (Date) mStartDate.getText().toString();
        Date endDate = (Date) mEndDate.getText();
        Log.e(TAG, "start: " + startDate.toString());
        Log.e(TAG, "end " + endDate.toString());
        */
        Date startDate = new Date();
        Date endDate = new Date();
        boolean allIsWell = true;

        startDate = retrieveDate(mStartDate.getText().toString().trim(), true);
        endDate = retrieveDate(mEndDate.getText().toString().trim(), false);
        allIsWell = startDate != null && endDate != null;

        if (startDate.after(endDate))
        {
            Snackbar.make(findViewById(android.R.id.content), "Start date must be before end date.", Snackbar.LENGTH_SHORT).show();
            allIsWell = true;
        }

        // Use the dates given to retrieve all of the logs.
        // Best way: iterate through all dates from start to end. Print out all logs from each day.

        if (allIsWell) {
            Log.e(TAG, "start: " + startDate.toString());
            Log.e(TAG, "end " + endDate.toString());

            String feedText = null, exerciseText = null, enclosureText, behaviorText;
            int dietPos, exercisePos, enclosurePos, behaviorPos;

            // Make the report here after Lauren responds to the email.
            // We have four lists on different time tracks. We need to write a piece of code that
            // breaks them up by day and stitches them into one journal.

            // To start, find the starting position in each list:
            dietPos = getPosition(feedTaskLogs, startDate.getTime());
            exercisePos = getPosition(exerciseTaskLogs, startDate.getTime());

            /**
            for (long time = startDate.getTime(); time <= endDate.getTime(); time+=86400000)
            {
                if (dateIsGood(feedTaskLogs, dietPos, time))
                {
                    FeedLog feedLog = feedLogs.get(dietPos);
                    feedText = feedLog.toString();
                    dietPos++;
                }
                if (dateIsGood(exerciseTaskLogs, exercisePos, time))
                {
                    ExerciseLog exerciseLog = exerciseLogs.get(exercisePos);
                    exerciseText = exerciseLog.toString();
                    exercisePos++;
                }
                if (feedText != null || exerciseText != null) {
                    // add to report
                    report += getDate(new Date(time).toString()) + "\n";
                    if (feedText != null) report += feedText + "\n";
                    if (exerciseText != null) report += exerciseText + "\n";
                    report += "\n";
                }


            }
             */

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, report);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // also why is this showing Google Drive...?
            // because other apps can also have the whole message thing. use the other thing instead.
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client to send this report."));
        }
    }

    private boolean dateIsGood (List<TaskLog> logs, int position, long time)
    {
        long completedTime = logs.get(position).getCompletedTime();
        return completedTime >= time && completedTime <= time + 86400000;
    }

    private String getDate (String verbose)
    {
        Pattern pattern = Pattern.compile("\\w{3} \\w+ \\d{2}");
        Matcher matcher = pattern.matcher(verbose);
        if (matcher.find())
        {
            return matcher.group();
        }
        return verbose;
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

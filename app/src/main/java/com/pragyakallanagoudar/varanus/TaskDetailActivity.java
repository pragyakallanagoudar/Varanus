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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.pragyakallanagoudar.varanus.adapter.TaskLogAdapter;
import com.pragyakallanagoudar.varanus.model.Task;
import com.pragyakallanagoudar.varanus.model.TaskType;
import com.pragyakallanagoudar.varanus.model.log.BehaviorLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class TaskDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    private static final String TAG = TaskDetailActivity.class.getSimpleName();

    public static final String KEY_TASK_ID = "key_task_id";
    public static final String KEY_RESIDENT_ID = "key_resident_id";

    // The components on the screen.
    private ImageView mImageView;
    private TextView mActivityTypeView;
    private TextView mFrequencyView;
    private EditText mCommentText;
    private TextView title;
    private Spinner mFoodType;
    private Spinner mFoodCount;
    private EditText mExerciseTime;
    private EditText mBehaviorText;

    // Database references and listeners
    private FirebaseFirestore mFirestore;
    private DocumentReference mTaskRef, mResidentRef;
    private ListenerRegistration mTaskRegistration;

    private TaskLog taskLog;

    // 4/15: Perhaps we only need one adapter class.
    private TaskLogAdapter mTaskLogAdapter;

    private String residentID;
    private TaskType type;
    private String logName;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()"); // DELETE LATER: to study the flow

        // Get task id from extras
        String taskID = getIntent().getExtras().getString(KEY_TASK_ID);
        residentID = getIntent().getExtras().getString(KEY_RESIDENT_ID);
        if (taskID == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_TASK_ID);
        }
        type = TaskType.valueOf(taskID.substring(0, taskID.indexOf('-')).toUpperCase());

        // Get logName and set content view according to task type (retrieved above)
        switch(type) {
            case FEED:
                logName = "FeedLog";
                setContentView(R.layout.activity_task_feed);
                mFoodType = findViewById(R.id.food_type_spinner);
                mFoodCount = findViewById(R.id.food_count_spinner);
                break;
            case BEHAVIOR:
                logName = "BehaviorLog";
                setContentView(R.layout.activity_task_behavior);
                mBehaviorText = findViewById(R.id.edit_behavior);
                break;
            case EXERCISE:
                logName = "ExerciseLog";
                setContentView(R.layout.activity_task_exercise);
                mExerciseTime = findViewById(R.id.edit_time);
                break;
            case CLEAN:
                logName = "CleanLog";
                setContentView(R.layout.activity_task_default);
                break;
            default:
                logName = ""; // this is a bit dangerous lol
                setContentView(R.layout.activity_task_default);
                break;
        }

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);
        title = findViewById(R.id.title);
        title.setText(type.toString() + " " + residentID.substring(residentID.indexOf('-') + 2));

        // ONGOING: make the different layouts for activity_task and choose according to type
        // mImageView = findViewById(R.id.task_image);
        // mActivityTypeView = findViewById(R.id.task_activityType);
        // mFrequencyView = findViewById(R.id.task_frequency);
        // mCommentText = findViewById(R.id.task_comments);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the log
        mResidentRef = mFirestore.collection("Guadalupe Residents").document(residentID);
        mTaskRef = mFirestore.collection("Guadalupe Residents").document(residentID)
                .collection("Tasks").document(taskID);

        Query taskLogQuery = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName);
        mTaskLogAdapter = new TaskLogAdapter(taskLogQuery, type);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart()");
        super.onStart();
        mTaskLogAdapter.startListening();
        mTaskRegistration = mTaskRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "OnStop()");
        mTaskLogAdapter.stopListening();

        if (mTaskRegistration != null) {
            mTaskRegistration.remove();
            mTaskRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_button:
                onSubmitClicked(v);
                break;
            case R.id.cancel_button:
                onCancelClicked(v);
                break;
        }
    }

    public void onSubmitClicked(View v)
    {
        // This is going to require some interesting changes...
        // 4/17: Trying to figure out how to make this ensure that all information
        // is logged before clicking the submit button.
        Log.e(TAG, "onSubmitClicked");

        taskLog.setCompletedTime(new Date().getTime());

        boolean emptyFields = false;
        boolean isProblem = false;

        try {
            switch (type) {
                case FEED:
                    String foodType = mFoodType.getSelectedItem().toString();
                    String foodCount = mFoodCount.getSelectedItem().toString();
                    emptyFields = foodType.equals("Food Type") || foodCount.equals("Food Count");
                    if (!emptyFields)
                    {
                        taskLog.setFoodName(foodType);
                        taskLog.setFoodCount(Integer.parseInt(foodCount));
                    }
                    break;
                case EXERCISE:
                    // default value: 0 (shown in hint)
                    try{
                        taskLog.setOutsideTime(Integer.parseInt(mExerciseTime.getText().toString()));
                    } catch (NumberFormatException e) {
                        taskLog.setOutsideTime(0);
                    }
                    break;
                case BEHAVIOR:
                    emptyFields = mBehaviorText.getText().toString().trim().equals("");
                    if (!emptyFields)
                        taskLog.setBehaviorText(mBehaviorText.getText().toString());
                    Log.e(TAG, "behavior text " + mBehaviorText.getText().toString());
                    break;
                default:
                    // do nothing: we will come back and resolve this
            }
        }  catch (Exception e) {
                isProblem = true;
                Snackbar problem = Snackbar.make(findViewById(android.R.id.content),
                        "This type of TaskLog cannot store this information. See logs.",
                        Snackbar.LENGTH_LONG);
                problem.show();
        }

        if (emptyFields) {
            Snackbar empty = Snackbar.make(findViewById(android.R.id.content),
                    "Please fill in all information.",
                    Snackbar.LENGTH_SHORT);
            empty.show();
        }

        if (!isProblem && !emptyFields)
        {
            onTaskLog(taskLog);
            finish();
        }
    }

    public void onCancelClicked(View view) {
        finish();
    }

    // 4/12: Perhaps we should move this, as well as mistake code, to the TaskLog class.
    private com.google.android.gms.tasks.Task<Void> addTaskLog(final DocumentReference taskRef,
                                                               final TaskLog tasklog) {
        Log.e(TAG, "addTaskLog");
        // Create reference for new feedLog, for use inside the transaction
        final DocumentReference taskLogRef = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName).document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Task task = transaction.get(taskRef)
                        .toObject(Task.class);

                // Set new timestamp
                task.setLastCompleted(new Date().getTime());

                // Commit to Firestore
                transaction.set(taskRef, task);
                transaction.set(taskLogRef, tasklog);
                return null;
            }
        });
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e)
    {
        Log.e(TAG, "onEvent()");
        onTaskLoaded(snapshot.toObject(Task.class));
    }

    private void onTaskLoaded(Task task)
    {
        Log.e(TAG, "onTaskLoaded()");
        //taskType = task.getTaskType();

        // mActivityTypeView.setText("Activity Type: " + task.getActivityType());
        // mFrequencyView.setText("Task Frequency: " + task.getFrequency());

        /**
        // 4/12: We want to make design changes to this: have different xml files for FEED and CARE
        if(task.getTaskType().equals("Feed"))
        {
            mFoodType.setVisibility(View.VISIBLE);
            mFoodCount.setVisibility(View.VISIBLE);
        }
        else
        {
            mFoodType.setVisibility(View.GONE);
            mFoodCount.setVisibility(View.GONE);
        }
        */
        switch(type) {
            case FEED:
                taskLog = new FeedLog(0, "sample", 0);
                break;
            case EXERCISE:
                taskLog = new ExerciseLog(0, 0);
                break;
            case BEHAVIOR:
                taskLog = new BehaviorLog(0, "");
                break;
            default:
                taskLog = new TaskLog(0);
                break;
        }
    }

    public void onTaskLog(TaskLog tasklog)
    {
        // In a transaction, add the new log and update the aggregate totals
        addTaskLog(mTaskRef, tasklog)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "TaskLog added successfully.");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        //mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to add TaskLog.", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to log information.",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    // Code to hide the keyboard.
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

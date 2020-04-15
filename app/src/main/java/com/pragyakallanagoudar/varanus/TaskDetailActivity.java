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
import com.pragyakallanagoudar.varanus.adapter.FeedLogAdapter;
import com.pragyakallanagoudar.varanus.adapter.TasksAdapter;
import com.pragyakallanagoudar.varanus.model.log.BehaviorLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.Task;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class TaskDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    private static final String TAG = "TaskDetail";

    public static final String KEY_TASK_ID = "key_task_id";
    public static final String KEY_RESIDENT_ID = "key_resident_id";

    // The components on the screen.
    private ImageView mImageView;
    private TextView mActivityTypeView;
    private TextView mFrequencyView;
    private EditText mCommentText;
    private Spinner mFoodType;
    private Spinner mFoodCount;

    // Database references and listeners
    private FirebaseFirestore mFirestore;
    private DocumentReference mTaskRef, mResidentRef;
    private ListenerRegistration mTaskRegistration;

    private TaskLog taskLog;

    // 4/15: Perhaps we only need one adapter class.
    private FeedLogAdapter mFeedLogAdapter;

    private String residentId;
    private String taskType;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mImageView = findViewById(R.id.task_image);
        mActivityTypeView = findViewById(R.id.task_activityType);
        mFrequencyView = findViewById(R.id.task_frequency);
        mCommentText = findViewById(R.id.task_comments);
        mFoodType = findViewById(R.id.food_type_spinner);
        mFoodCount = findViewById(R.id.food_count_spinner);

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);

        // Get task id from extras
        String taskId = getIntent().getExtras().getString(KEY_TASK_ID);
        residentId = getIntent().getExtras().getString(KEY_RESIDENT_ID);
        if (taskId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_TASK_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the log
        mResidentRef = mFirestore.collection("Guadalupe Residents").document(residentId);
        mTaskRef = mFirestore.collection("Guadalupe Residents").document(residentId).collection("Tasks").document(taskId);

        // Get taskLogs
        Query feedLogQuery = mTaskRef.collection("Guadalupe Residents")
                .document(residentId).collection("FeedLog");
        // Query behaviourLogQuery = mTaskRef.collection("BehaviourLog");
        mFeedLogAdapter = new FeedLogAdapter(feedLogQuery);

        // BehaviourLogAdapter mBehaviourLogAdapter = new BehaviourLogAdapter(behaviourLogQuery);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFeedLogAdapter.startListening();
        mTaskRegistration = mTaskRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mFeedLogAdapter.stopListening();

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
        if (taskType != null)
        {
            if (taskType.equals("Feed") && taskLog instanceof FeedLog)
            {
                taskLog.setDescription(mCommentText.getText().toString());
                taskLog.setCompletedTime(new Date().getTime());

                try {
                    taskLog.setFoodName(mFoodType.getSelectedItem().toString());
                    taskLog.setFoodCount(mFoodCount.getSelectedItem().hashCode());
                } catch (Exception e) {
                    Snackbar.make(findViewById(android.R.id.content), "This type of TaskLog cannot accept food name and count. See logs.", Snackbar.LENGTH_LONG);
                }
            }
        }


        TasksAdapter.numTasks--;
        onTaskLog(taskLog);
        finish();
    }

    public void onCancelClicked(View view) {
        finish();
    }

    // 4/12: Perhaps we should move this, as well as mistake code, to the TaskLog class.
    private com.google.android.gms.tasks.Task<Void> addTaskLog(final DocumentReference taskRef,
                                                               final TaskLog tasklog) {
        // Create reference for new feedLog, for use inside the transaction
        final DocumentReference taskLogRef = mFirestore.collection("Guadalupe Residents")
                .document(residentId).collection("FeedLog").document();

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
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

        onTaskLoaded(snapshot.toObject(Task.class));
    }

    private void onTaskLoaded(Task task)
    {
        taskType = task.getTaskType();

        mActivityTypeView.setText("Activity Type: " + task.getActivityType());
        mFrequencyView.setText("Task Frequency: " + task.getFrequency());

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

        // set to default values
        //taskLog = new TaskLog("sample", task.getActivityType(), task.getDescription(),0,"sample enclosure",task.getFrequency(),"","",0);

        switch(task.getTaskType())
        {
            case "Feed":
                taskLog = new FeedLog(0, "default", 0);
                break;
            case "Clean":
                taskLog = new TaskLog(0, "");
                break;
            case "Behavior":
                taskLog = new BehaviorLog(0, "");
                break;
            case "Exercise":
                taskLog = new ExerciseLog(0, 0);
                break;
            default:
                taskLog = new TaskLog(0, "");
                break;
        }
    }

    public void onTaskLog(TaskLog tasklog)
    {
        // In a transaction, add the new rating and update the aggregate totals
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

package com.pragyakallanagoudar.varanus.activity;

// All import statements listed below

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.adapter.TaskLogAdapter;
import com.pragyakallanagoudar.varanus.model.Task;
import com.pragyakallanagoudar.varanus.model.TaskType;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;


import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * TaskDetailActivity is the activity that comes up for data entry after completing a task.
 */

public class TaskDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    // Tag constant for Log entries
    private static final String TAG = TaskDetailActivity.class.getSimpleName();

    public static final String KEY_TASK_ID = "key_task_id";
    public static final String KEY_RESIDENT_ID = "key_resident_id";
    public static final String KEY_RESIDENT_NAME = "key_resident_name";

    // The components on the screen.
    private TextView title; // the title of the form
    private TextView description; // the description in the form to show the user
    private Spinner mFoodType; // select the type of food being fed to the animal
    private Spinner mFoodCount; // select the amount of food being given
    private EditText mExerciseTime; // enter the amount of time spent outside
    private EditText mBehaviorText; // provide a summary of the behavior of the animal
    private Spinner mCleanLevel; // select the level of being cleaned

    // Database references and listeners
    private FirebaseFirestore mFirestore; // the Firebase database reference
    private DocumentReference mTaskRef; // the reference to the task in the database
    private ListenerRegistration mTaskRegistration; // ListenerRegistration instance

    private TaskLog taskLog;
    private TaskLogAdapter mTaskLogAdapter;

    private String residentID;
    private String residentName;
    private TaskType type;
    private String logName;
    private String oldTaskID; // empty String if task is black

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()"); // DELETE LATER: to study the flow

        // Get task id from extras
        String taskID = getIntent().getExtras().getString(KEY_TASK_ID);
        residentID = getIntent().getExtras().getString(KEY_RESIDENT_ID);
        residentName = getIntent().getExtras().getString(KEY_RESIDENT_NAME);
        if (taskID == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_TASK_ID);
        }
        type = TaskType.valueOf(taskID.substring(0, taskID.indexOf('-')).toUpperCase());

        Resources res = getResources();
        String descriptionText, task, info = "";
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        // user = user.substring(user.indexOf(" "));

        Log.e(TAG, type.toString());

        // Get logName and set content view according to task type (retrieved above)
        switch(type) {
            case FEED:
                logName = "FeedLog";
                setContentView(R.layout.activity_task_feed);
                mFoodType = findViewById(R.id.food_type_spinner);
                mFoodCount = findViewById(R.id.food_count_spinner);
                task = "feeding " + residentName;
                info = "select the food type and count";
                descriptionText = String.format(res.getString(R.string.task_description), user, task, info);
                break;
            case BEHAVIOR:
                logName = "BehaviorLog";
                setContentView(R.layout.activity_task_behavior);
                mBehaviorText = findViewById(R.id.edit_behavior);
                task = "reporting " + residentName + "'s behavior";
                info = "enter a short summary";
                descriptionText = String.format(res.getString(R.string.task_description), user, task, info);
                break;
            case EXERCISE:
                logName = "ExerciseLog";
                setContentView(R.layout.activity_task_exercise);
                mExerciseTime = findViewById(R.id.edit_time);
                task = "taking " + residentName + " outside";
                info = "enter the time spent outside";
                descriptionText = String.format(res.getString(R.string.task_description), user, task, info);
                break;
            case CLEAN:
                //logName = "CleanLog";
                logName = "EnclosureLog";
                setContentView(R.layout.activity_task_clean);
                mCleanLevel = findViewById(R.id.select_clean_level);
                task = "cleaning " + residentName + "'s enclosure";
                info = "select the level of cleaning done";
                descriptionText = String.format(res.getString(R.string.task_description), user, task, info);
                break;
            case ENRICH:
                Log.e(TAG, "yo???");
                logName = "EnclosureLog";
                setContentView(R.layout.activity_task_default);
                task = "enriching " + residentName + "'s enclosure";
                descriptionText = String.format(res.getString(R.string.default_task_description), user, task);
                break;
            default:
                logName = ""; // this is a bit dangerous lol
                setContentView(R.layout.activity_task_default);
                task = "completing this task for " + residentID.substring(residentID.indexOf("-") + 2);
                descriptionText = String.format(res.getString(R.string.default_task_description), user, task);
                break;
        }

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);
        title = findViewById(R.id.title);
        title.setText(type.toString() + " " + residentName);

        description = findViewById(R.id.description);
        description.setText(descriptionText);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Get reference to the log
        mTaskRef = mFirestore.collection("Guadalupe Residents").document(residentID)
                .collection("Tasks").document(taskID);

        Query taskLogQuery = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection(logName);
        mTaskLogAdapter = new TaskLogAdapter(taskLogQuery, type);

        oldTaskID = ""; // initialize with "" (assume task is black)
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
    public void onClick (View v) {
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

    @Override
    public void onEvent (DocumentSnapshot snapshot, FirebaseFirestoreException e)
    {
        Log.e(TAG, "onEvent()");
        onTaskLoaded(snapshot.toObject(Task.class));
    }

    /**
     * Once the task is loaded, create the taskLog object accordingly.
     * @param task      Task object loaded from the database
     */
    private void onTaskLoaded(Task task)
    {
        Log.e(TAG, "onTaskLoaded()");

        // gray task: retrieve the last object
        if ((task.getLastCompleted() > (new Date()).getTime() - 84600000))
        {
            oldTaskID = task.getLastLogID(); // define the ID

            // Retrieve the document
            DocumentReference docRef = mFirestore.collection("Guadalupe Residents")
                    .document(residentID).collection(logName).document(task.getLastLogID());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            // If successfully retrieved, load data from document
                            loadLastLog(document);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        // black task: create a default object
        } else {
            switch (type) {
                case FEED:
                    taskLog = new FeedLog(0, "sample", 0);
                    break;
                case EXERCISE:
                    Log.e(TAG, "Making the Exercise flavor TaskLog object.");
                    taskLog = new ExerciseLog(0, 0);
                    break;
                case BEHAVIOR:
                    taskLog = new TextLog(0, "");
                    break;
                case ENRICH:
                case CLEAN:
                    Log.e(TAG, "Are we going in here?");
                    taskLog = new EnclosureLog(0, type, 0);
                    break;
                default:
                    taskLog = new TaskLog(0);
                    break;
            }
        }
    }


    /**
     * If the task is gray and the last log document has been successfully retrieved.
     * load the data here.
     * @param document      the retrieved most recent DocumentSnapshot object
     */
    public void loadLastLog (DocumentSnapshot document) {
        try {
            switch (type) {
                case FEED:
                    taskLog = document.toObject(FeedLog.class);
                    FeedLog feedLog = (FeedLog)taskLog;
                    mFoodType.setSelection(getIndex(mFoodType, feedLog.getFoodName()));
                    mFoodCount.setSelection(getIndex(mFoodCount, feedLog.getFoodCount() + ""));
                    break;
                case EXERCISE:
                    taskLog = document.toObject(ExerciseLog.class);
                    ExerciseLog exerciseLog = (ExerciseLog)taskLog;
                    mExerciseTime.setText(exerciseLog.getOutsideTime() + "");
                    Log.e(TAG, exerciseLog.getOutsideTime() + "");
                    break;
                case CLEAN:
                    taskLog = document.toObject(EnclosureLog.class);
                    EnclosureLog enclosureLog = (EnclosureLog)taskLog;
                    mCleanLevel.setSelection(enclosureLog.getCleanLevel());
                    break;
                case ENRICH:
                    taskLog = document.toObject(EnclosureLog.class);
                    break;
                case BEHAVIOR:
                    taskLog = document.toObject(TextLog.class);
                    TextLog behaviorLog = (TextLog)taskLog;
                    mBehaviorText.setText(behaviorLog.getBehaviorText());
                    break;
            }
        } catch (Exception e) {
            // If an Exception was thrown, display this message in a snackbar.
            Snackbar.make(findViewById(android.R.id.content), "Failed to log information.",
                    Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Get the position of a String in the array of options provided by a given spinner.
     * @param spinner       The spinner with the array of options
     * @param myString      The option of which we require the index
     * @return
     */
    private int getIndex (Spinner spinner, String myString)
    {
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    /**
     * This method is called when the submit button is clicked.
     * @param v     the View object
     */
    public void onSubmitClicked(View v)
    {
        Log.e(TAG, "onSubmitClicked");

        // set completed time of the taskLog to now.
        taskLog.setCompletedTime(new Date().getTime());

        // set user to name of current user
        taskLog.setUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        // Boolean switches for two possible types of errors
        boolean emptyFields = false; // all fields have not been filled.
        boolean wrongLog = false; // wrong type of TaskLog used (technically, should not be possible.)

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
                    try {
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
                case CLEAN:
                    // default value: "Not Cleaned"
                    taskLog.setCleanLevel(mCleanLevel.getSelectedItemPosition());
                    break;
                default:
                    // do nothing: we will come back and resolve this
            }
        }  catch (Exception e) {
            // wrong type of TaskLog
                wrongLog = true;
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

        if (!wrongLog && !emptyFields) // everything is fine.
        {
            onTaskLog(taskLog);
            finish();
        }
    }

    /**
     *
     * @param tasklog       TaskLog instance
     */
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

    /**
     * Add the TaskLog instance to the database.
     * @param taskRef
     * @param tasklog
     * @return
     */
    private com.google.android.gms.tasks.Task<Void> addTaskLog(final DocumentReference taskRef,
                                                               final TaskLog tasklog) {
        Log.e(TAG, "addTaskLog");

        // Create reference for new TaskLog, for use inside the transaction
        final DocumentReference taskLogRef;

        if (oldTaskID.equals("")) // black task
        {
            taskLogRef = mFirestore.collection("Guadalupe Residents")
                    .document(residentID).collection(logName).document();
        }
        else // gray task
        {
            taskLogRef = mFirestore.collection("Guadalupe Residents")
                    .document(residentID).collection(logName).document(oldTaskID);
        }

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Log.e(TAG, "inside apply function");

                Task task = transaction.get(taskRef)
                        .toObject(Task.class);

                task.setLastCompleted(new Date().getTime());


                if (oldTaskID.equals("")) {
                    // Set new timestamp
                    // assert task != null;
                    task.setLastCompleted(new Date().getTime());
                }

                Log.e("hAppY", tasklog.toString());

                // assert task != null;
                task.setLastLogID(taskLogRef.getId());

                // Commit to Firestore
                transaction.set(taskRef, task);
                transaction.set(taskLogRef, tasklog);
                return null;
            }
        });
    }

    public void onCancelClicked(View view) {
        finish();
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

package com.pragyakallanagoudar.varanus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

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

import java.util.Calendar;


public class TaskDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot> {

    private static final String TAG = "TaskDetail";

    public static final String KEY_TASK_ID = "key_task_id";

    private ImageView mImageView;
    private TextView mActivityTypeView;
    private TextView mSpeciesView;
    private TextView mFrequencyView;
    private TextView mDescriptionView;
    private TextView mEnclosureView;
    private EditText mCommentText;

    private FirebaseFirestore mFirestore;
    private DocumentReference mTaskRef;
    private ListenerRegistration mTaskRegistration;

    private TaskLogAdapter mTaskLogAdapter;

    interface TaskLogListener {

        void onTaskLog(TaskLog tasklog);

    }

    private TaskLogListener mTaskLogListener;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mImageView = findViewById(R.id.task_image);
        mActivityTypeView = findViewById(R.id.task_activityType);
        mSpeciesView = findViewById(R.id.task_species);
        mFrequencyView = findViewById(R.id.task_frequency);
        mDescriptionView = findViewById(R.id.task_description);
        mEnclosureView = findViewById(R.id.task_enclosure);
        mCommentText = findViewById(R.id.task_comments);

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);


        // Get restaurant ID from extras
        String taskId = getIntent().getExtras().getString(KEY_TASK_ID);
        if (taskId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_TASK_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mTaskRef = mFirestore.collection("Tasks").document(taskId);

        // Get taskLogs
        Query taskLogQuery = mTaskRef
                .collection("tasklog");
        mTaskLogAdapter = new TaskLogAdapter(taskLogQuery);
    }



    @Override
    public void onStart() {
        super.onStart();

        mTaskLogAdapter.startListening();
        mTaskRegistration = mTaskRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mTaskLogAdapter.stopListening();

        if (mTaskRegistration != null) {
            mTaskRegistration.remove();
            mTaskRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:
                onSubmitClicked(v);
                break;
            case R.id.cancel_button:
                onCancelClicked(v);
                break;
        }
    }

    public void onSubmitClicked(View v) {
        TaskLog tasklog = new TaskLog (
                mSpeciesView.getText().toString(),
                mActivityTypeView.getText().toString(),
                mDescriptionView.getText().toString(),
                Calendar.getInstance().getTime(),
                mEnclosureView.getText().toString(),
                mFrequencyView.getText().toString(),
                mCommentText.getText().toString());
                onTaskLog(tasklog);
        }

    public void onCancelClicked(View view) {
        //dismiss();
    }

    private com.google.android.gms.tasks.Task<Void> addTaskLog(final DocumentReference taskRef, final TaskLog tasklog) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference tasklogRef = taskRef.collection("tasklog")
                .document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Task task = transaction.get(taskRef)
                        .toObject(Task.class);

                // Set new timestamp
                task.setLastCompleted(Calendar.getInstance().getTime());

                // Commit to Firestore
                transaction.set(taskRef, task);
                transaction.set(tasklogRef, tasklog);

                return null;
            }
        });
    }


    /**
     * }).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onTaskLoaded(snapshot.toObject(Task.class));
    }

    private void onTaskLoaded(Task task) {

        mActivityTypeView.setText(task.getActivityType());
        mSpeciesView.setText(task.getSpecies());
        mFrequencyView.setText(task.getFrequency());
        mDescriptionView.setText(task.getDescription());
        mEnclosureView.setText(task.getEnclosure());

        // Background image
        Glide.with(mImageView.getContext())
                .load(task.getPhoto())
                .into(mImageView);
    }

    //@Override
    public void onTaskLog(TaskLog tasklog) {
        // In a transaction, add the new rating and update the aggregate totals
        addTaskLog(mTaskRef, tasklog)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        //mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

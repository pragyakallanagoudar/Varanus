package com.pragyakallanagoudar.varanus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.utilities.Utils;
import com.pragyakallanagoudar.varanus.model.log.TextLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * The general activity to show logistical information from the users of the app.
 */

public class GeneralActivity extends AppCompatActivity implements View.OnClickListener  {

    public static final String TAG = GeneralActivity.class.getSimpleName();
    final ArrayList<TextLog> logs = new ArrayList<TextLog>(); // the list of general text logs

    public TextView generalTextView; // the text journals with general information
    public EditText generalEditText; // the edit text for the user to add to the general logs

    public FirebaseFirestore mFirestore; // Firebase database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        generalTextView = findViewById(R.id.general_text_view);

        //findViewById(R.id.add_text_log).setOnClickListener(this);
        generalEditText = findViewById(R.id.general_edit_text);

        findViewById(R.id.submit_button).setOnClickListener(this);
        //findViewById(R.id.cancel_button).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("General");

        refreshDisplay();
    }

    /**
     * Refresh the display when loading in the logs and every time the user enters a log of their own.
     */
    private void refreshDisplay()
    {
        Query mQueryLogs = FirebaseFirestore.getInstance().collection("General")
                .orderBy("completedTime", Query.Direction.DESCENDING);
        if (logs.size() > 0) {
            logs.subList(0, logs.size()).clear();
        }

        mQueryLogs
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                logs.add(document.toObject(TextLog.class));
                            }
                            Log.e(TAG, "Successfully retrieved all logs.");
                            fillInText(logs);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Fill in the text from the general text logs in the database.
     * @param logs
     */
    private void fillInText(ArrayList<TextLog> logs)
    {

        String source = new String();

        for (TextLog log : logs)
        {
            Date date = new Date(log.getCompletedTime());
            String dateString = Utils.getDate(date.toString());
            String user = Utils.getFirstName(log.getUser());
            // make the source string in HTML to be cool
            source += "<b>[" + user + " on " +  dateString + "]</b> " + log.getBehaviorText() + "<br><br>";
        }

        generalTextView.setText(Html.fromHtml(source));
    }

    /**
     * When the button to submit the log is clicked, enter in the data in the form.
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        if (!generalEditText.getText().toString().trim().equals("")) {
            TextLog log = new TextLog(Calendar.getInstance().getTime().getTime(), generalEditText.getText().toString());
            log.setUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            FirebaseFirestore.getInstance().collection("General").document().set(log);
            hideKeyboard();
            generalEditText.setText("");
            refreshDisplay();
        }
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

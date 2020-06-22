package com.pragyakallanagoudar.varanus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Utils;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;
import com.pragyakallanagoudar.varanus.model.log.TextLog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = GeneralActivity.class.getSimpleName();
    final ArrayList<TextLog> logs = new ArrayList<TextLog>();

    public TextView generalTextView;
    public EditText generalEditText;

    public FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_communication);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        generalTextView = findViewById(R.id.general_text_view);
        generalEditText = findViewById(R.id.general_edit_text);

        findViewById(R.id.submit_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);

        refreshDisplay();
    }

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

    @Override
    public void onClick(View v)
    {
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
        TextLog log = new TextLog(Calendar.getInstance().getTime().getTime(), generalEditText.getText().toString());
        log.setUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        FirebaseFirestore.getInstance().collection("General").document().set(log);
        hideKeyboard();
        generalEditText.setText("");
        refreshDisplay();
    }

    // Code to hide the keyboard.
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onCancelClicked (View v)
    {
        finish();
    }


}

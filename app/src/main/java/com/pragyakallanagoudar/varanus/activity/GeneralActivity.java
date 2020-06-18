package com.pragyakallanagoudar.varanus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.log.EnclosureLog;
import com.pragyakallanagoudar.varanus.model.log.ExerciseLog;
import com.pragyakallanagoudar.varanus.model.log.FeedLog;
import com.pragyakallanagoudar.varanus.model.log.TaskLog;
import com.pragyakallanagoudar.varanus.model.log.TextLog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralActivity extends AppCompatActivity {

    public static final String TAG = GeneralActivity.class.getSimpleName();
    final ArrayList<TextLog> logs = new ArrayList<TextLog>();

    public TextView generalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_communication);

        generalTextView = findViewById(R.id.general_text_view);

        Query mQueryLogs = FirebaseFirestore.getInstance().collection("General")
                .orderBy("completedTime", Query.Direction.DESCENDING);
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
            String dateString = getDate(date.toString());
            String user = log.getUser();
            // make the source string in HTML to be cool
            source += "<b>[" + user + " on " +  dateString + "]</b> " + log.getBehaviorText() + "<br>";
        }

        generalTextView.setText(Html.fromHtml(source));
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
}

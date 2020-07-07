package com.pragyakallanagoudar.varanus.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Resident;
import com.pragyakallanagoudar.varanus.model.log.TextLog;
import com.pragyakallanagoudar.varanus.utilities.Utils;
import com.pragyakallanagoudar.varanus.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * The activity to add a new animal to the database.
 */

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener  {

    public static final String TAG = AddAnimalActivity.class.getSimpleName();
    final ArrayList<TextLog> logs = new ArrayList<TextLog>(); // the list of general text logs

    public EditText nameEditText; // the edit text for the user to add to the general logs
    public FloatingActionButton uploadButton; // the button to upload an image
    public Spinner locationSpinner; // the spinner to select the location in the Center
    public Spinner speciesSpinner; // the spinner to select the species of the animal

    public FirebaseFirestore mFirestore; // Firebase database instance

    public String newResidentID;

    /**
     * Instantiate the field variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_new);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        //findViewById(R.id.add_text_log).setOnClickListener(this);
        nameEditText = findViewById(R.id.name_edit_text);

        uploadButton = findViewById(R.id.upload_button);

        locationSpinner = findViewById(R.id.location_spinner);
        speciesSpinner = findViewById(R.id.species_spinner);

        findViewById(R.id.submit_button).setOnClickListener(this);
        //findViewById(R.id.cancel_button).setOnClickListener(this)

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add New Animal");

        Resources res = getResources();
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
        TextView description = findViewById(R.id.description);
        String descriptionText = String.format(res.getString(R.string.add_animal_description), user);
        description.setText(descriptionText);
    }

    /**
     * When either the submit button or the cancel buttons are clicked...
     * @param v
     */
    @Override
    public void onClick (View v) {
        switch (v.getId())
        {
            case R.id.submit_button:
                addAnimal();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }

    /**
     * Add a new animal to the database by retrieving the information from the EditText components.
     */
    public void addAnimal()
    {
        String name = nameEditText.getText().toString();
        String enclosure = locationSpinner.getSelectedItem().toString();
        String species = speciesSpinner.getSelectedItem().toString();
        Resident resident = new Resident();
        resident.setName(name);
        resident.setEnclosure(enclosure);
        resident.setSpecies(species);
        newResidentID = species.toLowerCase() + "-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID).set(resident);
        addTasks(species);
        hideKeyboard();
        finish();
    }

    public void addTasks(String species)
    {
        // make and add the feed task
        Task feedTask = new Task("CARE", 0, "Feed", "daily", "");
        String taskId = "feed-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(feedTask);

        // make and add the behavior task
        Task behaviorTask = new Task("ALERT", 0, "Behavior", "daily", "");
        taskId = "behavior-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(behaviorTask);

        // make and add the clean task
        Task cleanTask = new Task("CARE", 0, "Clean", "daily", "");
        taskId = "clean-" + Utils.getRandomID();
        FirebaseFirestore.getInstance().collection("Guadalupe Residents").document(newResidentID)
                .collection("Tasks").document(taskId).set(cleanTask);
        // Complete this after consulting Lauren's guide.
        Task enclosureTask = new Task("ENRICH", 0, "Enclosure Enrichment", "daily", "");
        Task exercise = new Task("ENRICH", 0, "Exercise", "daily", "");

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

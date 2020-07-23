package com.pragyakallanagoudar.varanus.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.adapter.ResidentAdapter;
import com.pragyakallanagoudar.varanus.model.Resident;
import com.pragyakallanagoudar.varanus.utilities.Utils;

import java.util.Collections;

/**
 * The list of animals that appear after clicking Animals on the landing
 */

// Comment Status: (2) all methods headed, don't really have much complex code
public class AnimalsActivity extends AppCompatActivity implements
        ResidentAdapter.OnResidentSelectedListener, View .OnClickListener{

    private FirebaseFirestore mFirestore; // reference to Cloud Firestore database
    private Query mQueryResidents; // query to the database to load tasks
    private RecyclerView mAnimalsRecycler; // reference to RecyclerView
    private ResidentAdapter mAdapter; // the reference to ResidentAdapter

    private Intent mainIntent; // the Intent instance to the MainActivity

    private FloatingActionButton addAnimalButton;

    private static final int RC_SIGN_IN = 9001;

    private static String TAG = MainActivity.class.getSimpleName();

    // READ THIS: It seems like this class will have some major changes when the
    // admin controls come into picture. Add more comments then.

    /**
     * Instantiate all the variables here.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        mainIntent = new Intent(this, MainActivity.class);

        // Initialize the database references
        mAnimalsRecycler = findViewById(R.id.animal_recyclerview);
        initFirestore();
        initRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Animals");

        addAnimalButton = findViewById(R.id.add_animal);
        if (!Utils.adminControls) addAnimalButton.setVisibility(View.INVISIBLE);
        addAnimalButton.setOnClickListener(this);
    }

    /**
     * Initialize the Firestore variables.
     */
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // get the first 50 documents from the collection Guadalupe Residents
        mQueryResidents = mFirestore.collection("Guadalupe Residents")
                .limit(50).orderBy("enclosure");

        // to try the experimental version, replace "Guadalupe Residents" with "Experimental" above
    }

    /**
     * Initialize the recycler view variables.
     */
    private void initRecyclerView() {
        Log.e(TAG, "initRecyclerView()");
        mAdapter = new ResidentAdapter(mQueryResidents, this);
        mAnimalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAnimalsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    /**
     * When a resident is selected, get it as an instance of the Resident class and start the TabbedTasks intent.
     * @param resident      the DataSnapshot that keys to the resident in question.
     */
    @Override
    public void OnResidentSelected(DocumentSnapshot resident)
    {
        Resident resClass = resident.toObject(Resident.class);
        // Go to the details page for the selected resident
        Intent intent = new Intent(this, TabbedTasks.class);
        intent.putExtra(TabbedTasks.RESIDENT_ID, resident.getId());
        intent.putExtra(TabbedTasks.RESIDENT_NAME, resClass.getName());
        startActivity(intent);
    }

    /**
     * This is called when the add animal button is clicked.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        startActivity(new Intent(getApplicationContext(), AddAnimalActivity.class));
    }

}

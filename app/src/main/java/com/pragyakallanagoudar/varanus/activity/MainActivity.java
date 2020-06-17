package com.pragyakallanagoudar.varanus.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.pragyakallanagoudar.varanus.R;

import java.util.Collections;
import com.firebase.ui.auth.AuthUI;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The MainActivity class is where everything starts.
 */

public class MainActivity extends AppCompatActivity implements
    /**ResidentAdapter.OnResidentSelectedListener,*/ View.OnClickListener {

    /**
    private FirebaseFirestore mFirestore; // reference to Cloud Firestore database
    private Query mQueryResidents; // query to the database to load tasks
    private RecyclerView mAnimalsRecycler; // reference to RecyclerView
    private ResidentAdapter mAdapter; // ???

    private static final int RC_SIGN_IN = 9001;

    private static String TAG = MainActivity.class.getSimpleName();
     */

    private static final int RC_SIGN_IN = 9001;

    public static final boolean SIGN_OUT = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        boolean signOut;

        try {
            signOut = getIntent().getExtras().getBoolean(String.valueOf(SIGN_OUT));
        } catch (NullPointerException e) {
            signOut = false;
        }

        if (signOut && shouldStartSignIn()) startSignIn();

        findViewById(R.id.animals).setOnClickListener(this);
        findViewById(R.id.balance).setOnClickListener(this);
        findViewById(R.id.general).setOnClickListener(this);

        /**
        // Initialize the database references
        mAnimalsRecycler = findViewById(R.id.animal_recyclerview);
        initFirestore();
        initRecyclerView(); */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    /**
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // get the first 50 documents from the collection Guadalupe Residents
        mQueryResidents = mFirestore.collection("Guadalupe Residents")
                .limit(50).orderBy("enclosure");

        // to try the experimental version, replace "Guadalupe Residents" with "Experimental" above
    }

    private void initRecyclerView() {
        Log.e(TAG, "initRecyclerView()");
        mAdapter = new ResidentAdapter(mQueryResidents, this);
        mAnimalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAnimalsRecycler.setAdapter(mAdapter);
    }
     */

    @Override
    public void onStart() {
        super.onStart();

        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        /**
        if (mAdapter != null) {
            mAdapter.startListening();
        }
         */
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.animals:
                Intent animalIntent = new Intent(this, AnimalsActivity.class);
                startActivity(animalIntent);
                break;
            case R.id.balance:
                Intent balanceIntent = new Intent(this, BalanceActivity.class);
                startActivity(balanceIntent);
                break;
            case R.id.general:
                Intent generalIntent = new Intent(this, GeneralActivity.class);
                startActivity(generalIntent);
                break;
        }

    }

    /**
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
    */

    private boolean shouldStartSignIn() {
        Log.e(String.valueOf(FirebaseAuth.getInstance().getCurrentUser()), MainActivity.class.getSimpleName());
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Log.e("hi", MainActivity.class.getSimpleName());
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                new AlertDialog.Builder(this)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                if (shouldStartSignIn()) startSignIn();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                        break;
            case R.id.unlock_controls:
                if (item.getTitle().equals("Unlock Admin Controls")) {
                    item.setTitle("Lock Admin Controls");
                } else {
                    item.setTitle("Unlock Admin Controls");
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

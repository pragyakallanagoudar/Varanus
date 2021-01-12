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
import com.pragyakallanagoudar.varanus.utilities.Utils;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The MainActivity class is where everything starts: the landing page, where you can
 * access animal tasks/profiles, general information, and gift card balance.
 */

public class MainActivity extends AppCompatActivity implements
    /**ResidentAdapter.OnResidentSelectedListener,*/ View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    public static final boolean SIGN_OUT = false;

    /**
     * Instantiate the variables.
     * @param savedInstanceState
     */
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Varanus");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

    }

    /**
     * When one of the buttons on the landing page is clicked...
     * @param view
     */
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
     * @return      whether the current user is null, which means the signin activity should start
     */
    private boolean shouldStartSignIn() {
        //Log.e(String.valueOf(FirebaseAuth.getInstance().getCurrentUser()), MainActivity.class.getSimpleName());
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    /**
     * Start the built-in sign in activity.
     */
    private void startSignIn() {
        // Sign in with FirebaseUI
        //Log.e("hi", MainActivity.class.getSimpleName());
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

    /**
     * When an option from the menu is selected, go through this method to perform the appropriate action.
     * @param item
     * @return
     */
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
                    Utils.adminControls = true;
                    item.setTitle("Lock Admin Controls");
                } else {
                    Utils.adminControls = false;
                    item.setTitle("Unlock Admin Controls");
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.pragyakallanagoudar.varanus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.adapter.ResidentAdapter;
import com.pragyakallanagoudar.varanus.model.Resident;


import java.util.Collections;
import com.firebase.ui.auth.AuthUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The MainActivity class is where everything starts.
 */

public class MainActivity extends AppCompatActivity implements
    ResidentAdapter.OnResidentSelectedListener {

        private FirebaseFirestore mFirestore; // reference to Cloud Firestore database
        private Query mQueryResidents; // query to the database to load tasks
        private RecyclerView mAnimalsRecycler; // reference to RecyclerView
        private ResidentAdapter mAdapter; // ???

        private static final int RC_SIGN_IN = 9001;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            // Initialize the database references
            mAnimalsRecycler = findViewById(R.id.animal_recyclerview);
            initFirestore();
            initRecyclerView();

        }
        private void initFirestore() {
            mFirestore = FirebaseFirestore.getInstance();

            // get the first 50 documents from the collection Guadalupe Residents
            mQueryResidents = mFirestore.collection("Guadalupe Residents")
                    .limit(50);

            // to try the experimental version, replace "Guadalupe Residents" with "Experimental" above
        }

        private void initRecyclerView() {
            mAdapter = new ResidentAdapter(mQueryResidents, this);
            mAnimalsRecycler.setLayoutManager(new LinearLayoutManager(this));
            mAnimalsRecycler.setAdapter(mAdapter);
        }

        @Override
        public void onStart() {
            super.onStart();

            if (shouldStartSignIn()) {
                startSignIn();
                return;
            }

            if (mAdapter != null) {
                mAdapter.startListening();
            }
        }

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

    private boolean shouldStartSignIn() {
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }
}

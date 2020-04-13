package com.pragyakallanagoudar.varanus;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.adapter.ResidentAdapter;

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
        }

        private void initRecyclerView() {
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

    @Override
    public void OnResidentSelected(DocumentSnapshot resident) {

        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, TabbedTasks.class);
        intent.putExtra(TabbedTasks.RESIDENT_ID, resident.getId());
        startActivity(intent);
    }
}

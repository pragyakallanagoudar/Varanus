package com.pragyakallanagoudar.varanus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.adapter.ResidentAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements
    ResidentAdapter.OnResidentSelectedListener {

        private FirebaseFirestore mFirestore;
        private Query mQueryTasks;
        private RecyclerView mAnimalsRecycler;
        private ResidentAdapter mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mAnimalsRecycler = findViewById(R.id.animal_recyclerview);
            initFirestore();
            initRecyclerView();

        }
        private void initFirestore() {
            mFirestore = FirebaseFirestore.getInstance();
            mQueryTasks = mFirestore.collection("Guadalupe Residents")
                    .limit(50);
        }

        private void initRecyclerView() {

            mAdapter = new ResidentAdapter(mQueryTasks, this);
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
        intent.putExtra(TabbedTasks.KEY_RESIDENT_ID, resident.getId());
        startActivity(intent);
    }
}

package com.pragyakallanagoudar.varanus.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.adapter.TasksAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The fragment of active tasks
 */
public class FragmentActiveTasks extends Fragment implements
        TasksAdapter.OnTasksSelectedListener {

    private FirebaseFirestore mFirestore; // Cloud Firestore reference
    private Query mQueryActiveTasks; // query to the database to get active tasks
    private RecyclerView mActiveTasksRecycler; // RecylerView of active tasks
    private TasksAdapter mAdapter; // the TasksAdapter
    private String residentID; // name of resident?
    private String residentName; // the name of the resident
    private String residentSpecies;
    View v;

    public FragmentActiveTasks () {}

    public FragmentActiveTasks(String residentID, String residentName, String residentSpecies) {
        this.residentID = residentID;
        this.residentName = residentName;
        this.residentSpecies = residentSpecies;
    }

    /**
     * Set up the fragment with the active tasks.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.active_tasks_fragment_old, container,false);
        mActiveTasksRecycler = v.findViewById(R.id.active_tasks_recyclerview);
        mAdapter = new TasksAdapter(mQueryActiveTasks, this);
        mActiveTasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActiveTasksRecycler.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mQueryActiveTasks = mFirestore.collection("Guadalupe Residents")
                .document(residentID).collection("Tasks").orderBy("activityType");
        //initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    /**
     * When a task is selected, launch TaskDetailActivity to have the user enter the required info
     * @param tasks
     */
    @Override
    public void onTasksSelected(DocumentSnapshot tasks)
    {
        Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.KEY_TASK_ID, tasks.getId());
        intent.putExtra(TaskDetailActivity.KEY_RESIDENT_ID, residentID);
        intent.putExtra(TaskDetailActivity.KEY_RESIDENT_NAME, residentName);
        intent.putExtra(TaskDetailActivity.KEY_RESIDENT_SPECIES, residentSpecies);
        startActivity(intent);
    }
}

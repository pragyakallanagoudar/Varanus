package com.pragyakallanagoudar.varanus;


import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.adapter.TasksAdapter;

import java.time.LocalDate;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentActiveTasks extends Fragment implements
        View.OnClickListener,
        TasksAdapter.OnCheckBoxSelectedListener {

    View v;
    private FirebaseFirestore mFirestore;
    private Query mQueryActiveTasks;
    private RecyclerView mActiveTasksRecycler;
    private TasksAdapter mAdapter;

    public FragmentActiveTasks() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.active_tasks_fragment,container,false);
        mActiveTasksRecycler = v.findViewById(R.id.active_tasks_recyclerview);
        mAdapter = new TasksAdapter(mQueryActiveTasks, this);
        mActiveTasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActiveTasksRecycler.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirestore();
        //initRecyclerView();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQueryActiveTasks = mFirestore.collection("Tasks")
                .limit(50)
                .whereLessThan("lastCompleted", new Date());
                //.orderBy("species");
    }

    private void initRecyclerView() {

        mAdapter = new TasksAdapter(mQueryActiveTasks, this);
        mActiveTasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActiveTasksRecycler.setAdapter(mAdapter);
    }

    public void onClick (View view)
    {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onCheckBoxSelected(DocumentSnapshot tasks)
    {
        Intent intent = new Intent(v.getContext(),TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.KEY_TASK_ID, tasks.getId());

        startActivity(intent);

    }
}

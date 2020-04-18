package com.pragyakallanagoudar.varanus.model.old;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.TaskDetailActivity;
import com.pragyakallanagoudar.varanus.adapter.TasksAdapter;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// 4/12: Currently a copy of FragmentActiveTasks.

public class FragmentCompletedTasks extends Fragment implements
        View.OnClickListener,
        TasksAdapter.OnTasksSelectedListener {

    View v;
    private FirebaseFirestore mFirestore;
    private Query mQueryCompletedTasks;
    private RecyclerView mCompletedTasksRecycler;
    private TasksAdapter mAdapter;
    private String resident;


    public FragmentCompletedTasks(String residentId) {
        this.resident = residentId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.completed_tasks_fragment, container, false);
        mCompletedTasksRecycler = v.findViewById(R.id.completed_tasks_recyclerview);
        mAdapter = new TasksAdapter(mQueryCompletedTasks, this);
        mCompletedTasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCompletedTasksRecycler.setAdapter(mAdapter);
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
        mQueryCompletedTasks = mFirestore.collection("Tasks")
                .whereGreaterThan("lastCompleted", new Date().getTime() - 1000*12*60*60);
    }

    private void initRecyclerView() {

        mAdapter = new TasksAdapter(mQueryCompletedTasks, this);
        mCompletedTasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCompletedTasksRecycler.setAdapter(mAdapter);
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
    public void onTasksSelected(DocumentSnapshot tasks)
    {
        Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.KEY_TASK_ID, tasks.getId());
        startActivity(intent);

    }
}

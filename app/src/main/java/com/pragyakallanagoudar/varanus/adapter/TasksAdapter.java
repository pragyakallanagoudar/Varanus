package com.pragyakallanagoudar.varanus.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.task.CleanTask;
import com.pragyakallanagoudar.varanus.model.task.Task;
import com.pragyakallanagoudar.varanus.model.TaskType;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
 /* RecyclerView adapter for a list of Tasks.
  */

 // 4/12: May not be needed.

public class TasksAdapter extends FirestoreAdapter<TasksAdapter.ViewHolder> {

    public static final String LOG_TAG = TasksAdapter.class.getSimpleName();

    public interface OnTasksSelectedListener
    {
        void onTasksSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private OnTasksSelectedListener mListener;  // instance of above interface


    // constructor
    public TasksAdapter(Query query, OnTasksSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityView;
        TextView instructionView;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            activityView = itemView.findViewById(R.id.text_activity_type);
            instructionView = itemView.findViewById(R.id.text_task_instruction);

            layout = itemView.findViewById(R.id.task_layout);
        }


        public void bind(final DocumentSnapshot snapshot,
                         final OnTasksSelectedListener listener) {

            String idType = snapshot.getId().substring(0, snapshot.getId().indexOf("-"));
            Task task;
            Log.e(LOG_TAG, "PEANUTS " + idType);
            switch (idType) {
                case "clean":
                    task = snapshot.toObject(CleanTask.class);
                    break;
                default:
                    task = snapshot.toObject(Task.class);
                    break;
            }

            //.whereLessThan("lastCompleted", new Date().getTime() - 1000*12*60);

            assert task != null;
            // There are 8.46e+7 milliseconds in a day.
            if ((task.getLastCompleted() > (new Date()).getTime() - task.getFrequency()*43200000))
            {
                //Log.e(LOG_TAG, task.getTaskType() + " GRAY");
                // layout.setBackgroundColor(Color.LTGRAY);
                activityView.setTextColor(Color.LTGRAY);
                instructionView.setTextColor(Color.LTGRAY);
            }
            else
            {
                //Log.e(LOG_TAG, task.getTaskType() + " WHITE");
                activityView.setTextColor(Color.BLACK);
                instructionView.setTextColor(Color.BLACK);
            }

            activityView.setText(task.getActivityType());

            String taskID = snapshot.getId();
            TaskType type;

            try {
                type = TaskType.valueOf(taskID.substring(0, taskID.indexOf('-')).toUpperCase());
            } catch (StringIndexOutOfBoundsException e) {
                type = TaskType.OTHER;
            }

            String instruction = task.getDescription();

            if (instruction == null) {
                switch (type) {
                    case FEED:
                        instruction = "Feed.";
                        break;
                    case CLEAN:
                        instruction = "Clean enclosure.";
                        break;
                    case BEHAVIOR:
                        instruction = "Report unusual behavior.";
                        break;
                    case EXERCISE:
                        instruction = "Outside for some exercise.";
                        break;
                    case ENRICH:
                        instruction = "Enclosure enrichment.";
                        break;
                    default:
                        instruction = type.toString();
                        break;
                }
            }


            instructionView.setText(instruction);
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onTasksSelected(snapshot);
                    }
                }
            });
        }

    }
}


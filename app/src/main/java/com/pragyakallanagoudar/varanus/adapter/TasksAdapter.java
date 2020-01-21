package com.pragyakallanagoudar.varanus.adapter;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import com.pragyakallanagoudar.varanus.model.Task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 /* RecyclerView adapter for a list of Tasks.
  */

public class TasksAdapter extends FirestoreAdapter<TasksAdapter.ViewHolder> {

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

        /*ImageView imageView;
        TextView nameView;
        TextView numRatingsView;*/
        Button buttonView;
        TextView speciesView;
        TextView messageView;

        public ViewHolder(View itemView) {
            super(itemView);
            /*imageView = itemView.findViewById(R.id.tasks_item_image);
            nameView = itemView.findViewById(R.id.tasks_item_name);
            ratingBar = itemView.findViewById(R.id.tasks_item_rating);
            numRatingsView = itemView.findViewById(R.id.tasks_item_num_ratings);
            priceView = itemView.findViewById(R.id.tasks_item_price);*/
            speciesView = itemView.findViewById(R.id.textView5);
            messageView = itemView.findViewById(R.id.textView7);
            buttonView = itemView.findViewById(R.id.button);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnTasksSelectedListener listener) {

            Task Tasks = snapshot.toObject(Task.class);
            Resources resources = itemView.getResources();

            // TODO: fix the bug here. The code only retrieves the first 3 Task entries

            Log.e(TasksAdapter.class.getSimpleName(), Tasks.getMessage()); // DEBUGGING: only prints first three entries to logcat

            // Load image
            /*Glide.with(imageView.getContext())
                    .load(Tasks.getPhoto())
                    .into(imageView);*/


            speciesView.setText(Tasks.getSpecies());
            messageView.setText(Tasks.getMessage());
            /*ratingBar.setRating((float) Tasks.getAvgRating());
            cityView.setText(Tasks.getCity());
            categoryView.setText(Tasks.getCategory());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
                    Tasks.getNumRatings()));
            priceView.setText(TasksUtil.getPriceString(Tasks));*/

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


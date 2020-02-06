package com.pragyakallanagoudar.varanus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pragyakallanagoudar.varanus.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.pragyakallanagoudar.varanus.model.TaskLog;
import com.google.firebase.firestore.Query;



/**
 * RecyclerView adapter for a bunch of Ratings.
 */
public class TaskLogAdapter extends FirestoreAdapter<TaskLogAdapter.ViewHolder> {

    public TaskLogAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_animals_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(TaskLog.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

       /* TextView nameView;
        MaterialRatingBar ratingBar;
        TextView textView;*/

        public ViewHolder(View itemView) {
            super(itemView);
           /* nameView = itemView.findViewById(R.id.rating_item_name);
            ratingBar = itemView.findViewById(R.id.rating_item_rating);
            textView = itemView.findViewById(R.id.rating_item_text);*/
        }

        public void bind(TaskLog tasklog) {
            /*nameView.setText(rating.getUserName());
            ratingBar.setRating((float) rating.getRating());
            textView.setText(rating.getText());*/
        }
    }

}
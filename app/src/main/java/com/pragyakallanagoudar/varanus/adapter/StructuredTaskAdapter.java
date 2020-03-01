package com.pragyakallanagoudar.varanus.adapter;

public class StructuredTaskAdapter //extends FirestoreAdapter<StructuredTaskAdapter.ViewHolder>
{

    /**
    public static final String LOG_TAG = SpeciesHolderAdapter.class.getSimpleName(); // log tag for debugging purposes

    private List<TaskView> taskViewList;

    // the interface that will connect this to Tasks
    public interface OnStructuredListener
    {
        void onStructuredSelected(DocumentSnapshot tasks); // implement in subclasses
    }

    private StructuredTaskAdapter.OnStructuredListener mListener;  // instance of above interface

    // constructor
    public StructuredTaskAdapter(Query query, StructuredTaskAdapter.OnStructuredListener listener)
    {
        super(query);
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return taskViewList.get(position).getType();
    }

    @NonNull
    @Override
    // Create the view type based upon the type of TaskView.
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView;

        switch(viewType)
        {
            case TaskView.TYPE_SPECIES:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_task, parent, false);
                return new SpeciesHolder(itemView);

            default: // task
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_task, parent, false);
                return new TaskHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StructuredTaskAdapter.ViewHolder holder, int position)
    {
        switch (getItemViewType(position)) {
            case TaskView.TYPE_SPECIES:
                ((SpeciesHolder)holder).bind();
        }
        holder.bind(getSnapshot(position), mListener);
    }

    static abstract class ViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolder (View itemView)
        {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnStructuredListener listener) {}

    }

    static class SpeciesHolder extends ViewHolder
    {
        public SpeciesHolder (View itemView)
        {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnStructuredListener listener)
        {

        }
    }

    static class TaskHolder extends ViewHolder
    {
        public TaskHolder (View itemView)
        {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnStructuredListener listener)
        {

        }
    }
*/

}

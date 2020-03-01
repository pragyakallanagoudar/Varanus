package com.pragyakallanagoudar.varanus.model;

public interface TaskView
{
    int TYPE_SPECIES = 101; // constant indicating species label
    int TYPE_TASK = 102; // constant indicating task item

    int getType();
}

class SpeciesLabel implements TaskView
{
    public int getType()
    {
        return TYPE_SPECIES;
    }
}

class TaskItem implements TaskView
{
    @Override
    public int getType()
    {
        return TYPE_TASK;
    }
}
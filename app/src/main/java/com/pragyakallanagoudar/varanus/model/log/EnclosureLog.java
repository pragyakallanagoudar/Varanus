package com.pragyakallanagoudar.varanus.model.log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class EnclosureLog extends TaskLog
{
    public TaskType task;

    public EnclosureLog(long completedTime, TaskType task)
    {
        super(completedTime);
        this.task = task;
    }

    public EnclosureLog () {}

    @Override
    public void setTask (TaskType task)
    {
        this.task = task;
    }

    public TaskType getTask ()
    {
        return task;
    }
}

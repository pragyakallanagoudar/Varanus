package com.pragyakallanagoudar.varanus;

public class Task
{
    public String task_message;
    public TaskType type;
    public boolean default_task;

    public Task(String task_message, TaskType type, boolean default_task)
    {
        this.task_message = task_message;
        this.type = type;
        this.default_task = default_task;
    }

    // if not a default task, and user is an admin, task can be edited.
}

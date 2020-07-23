package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Task
{
    public String activityType; // the type of activity (CARE or ENRICH)
    public String taskType; // the type of task (FEED, CLEAN, BEHAVIOR, etc.)
    public long lastCompleted; // the time this task was last completed
    public String frequency; // the frequency at which this task should be completed TODO: make this work for things other than daily
    public String lastLogID; // the ID of the log for the last time this task was completed

    public Task() {}

    public Task(String activityType, long lastCompleted, String taskType, String frequency, String lastLogID)
    {

        this.activityType = activityType;
        this.lastCompleted = lastCompleted;
        this.taskType = taskType;
        this.frequency = frequency;
        this.lastLogID = lastLogID;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }

    public long getLastCompleted() { return lastCompleted; }

    public void setLastCompleted(long date) { this.lastCompleted = date; }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getLastLogID() { return lastLogID; }

    public void setLastLogID(String lastLogID) { this.lastLogID = lastLogID; }
}

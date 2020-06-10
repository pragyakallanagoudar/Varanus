package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Task
{
    public String activityType;
    public String taskType;
    public long lastCompleted;
    public String frequency;
    public String lastLogID;

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

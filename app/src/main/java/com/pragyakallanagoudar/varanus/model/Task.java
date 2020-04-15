package com.pragyakallanagoudar.varanus.model;

import java.lang.String;

public class Task
{
    public String activityType;
    public String taskType;
    public String description;
    public long lastCompleted;
    public String frequency;

    public Task() {}

    public Task(String activityType, String description, long lastCompleted, String taskType, String frequency)
    {

        this.activityType = activityType;
        this.description = description;
        this.lastCompleted = lastCompleted;
        this.taskType = taskType;
        this.frequency = frequency;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

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
}

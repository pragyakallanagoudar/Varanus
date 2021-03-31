package com.pragyakallanagoudar.varanus.model.task;

public class CleanTask extends Task
{
    public String location;

    public CleanTask() {}

    public CleanTask(String activityType, long lastCompleted, String taskType, int frequency, String lastLogID, String description, String location)
    {
        super(activityType, lastCompleted, taskType, frequency, lastLogID, description);
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String newLocation)
    {
        location = newLocation;
    }
}

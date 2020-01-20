package com.pragyakallanagoudar.varanus.model;

import java.util.Date;
import java.lang.String;

public class Task
{
    public String species;
    public TaskType activityType;
    public String message;
    public Date date;
    public boolean optional;
    public String enclosure;

    public Task() {}

    public Task(String species, String activityType, String message, Date date, boolean optional, String enclosure)
    {
        this.species = species;
        this.activityType = TaskType.valueOf(activityType); // convert the String to enum in code
        this.message = message;
        this.date = date;
        this.optional = optional;
        this.enclosure = enclosure;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public TaskType getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = TaskType.valueOf(activityType);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) { this.date = date; }
}

package com.pragyakallanagoudar.varanus.model;

import java.util.Date;
import java.lang.String;

public class Task
{
    public String species;
    public TaskType activityType;
    public String description;
    public Date lastCompleted;
    public String enclosure;
    public String frequency;

    public Task() {}

    public Task(String species, String activityType, String description, Date lastCompleted, String enclosure, String frequency)
    {
        this.species = species;
        this.activityType = TaskType.valueOf(activityType); // convert the String to enum in code
        this.description = description;
        this.lastCompleted = lastCompleted;
        this.enclosure = enclosure;
        this.frequency = frequency;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public Date getLastCompleted() {
        return lastCompleted;
    }

    public void setLastCompleted(Date date) { this.lastCompleted = lastCompleted; }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) { this.frequency = frequency; }
}

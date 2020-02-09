package com.pragyakallanagoudar.varanus.model;

import java.util.Date;
import java.lang.String;

public class Task
{
    public String species;
    public String activityType;
    public String description;
    public Date lastCompleted;
    public String enclosure;
    public String frequency;
    public String photo;

    public Task() {}

    public Task(String species, String activityType, String description, Date lastCompleted, String enclosure, String frequency, String photo)
    {
        this.species = species;
        this.activityType = activityType; // convert the String to enum in code
        this.description = description;
        this.lastCompleted = lastCompleted;
        this.enclosure = enclosure;
        this.frequency = frequency;
        this.photo = photo;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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

    public Date getLastCompleted() {
        return lastCompleted;
    }

    public void setLastCompleted(Date date) { this.lastCompleted = lastCompleted; }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) { this.enclosure = enclosure; }

    public String getPhoto() {
        return photo;
    }
}

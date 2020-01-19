package com.pragyakallanagoudar.varanus.model;

import java.util.Date;
import java.lang.String;

public class Task
{
    public String species;
    public String activityType;
    public String message;
    public Date date;

    public Task() {}

    public Task(String species, String activityType, String message, Date date )
    {
        this.species = species;
        this.activityType = activityType;
        this.message = message;
        this.date = date;
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

    public void setActivityType(String activityType) {
        this.activityType = activityType;
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

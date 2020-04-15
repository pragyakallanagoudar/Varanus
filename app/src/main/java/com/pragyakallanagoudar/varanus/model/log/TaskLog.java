package com.pragyakallanagoudar.varanus.model.log;

import android.util.Log;

public class TaskLog
{

    public long completedTime;
    //public String completedBy;
    public String description;
    public final String LOG_TAG = TaskLog.class.getSimpleName();

    public TaskLog(long completedTime, /*String completedBy,*/ String description)
    {
        this.description = description;
        //this.completedBy = completedBy;
        this.completedTime = completedTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }

    public void setFoodCount(int foodCount) throws Exception
    {
        Log.e(LOG_TAG, "This type of TaskLog will not accept food count.");
        throw new Exception();
    }

    public void setFoodName(String foodName) throws Exception
    {
        Log.e(LOG_TAG, "This type of TaskLog will not accept food name.");
        throw new Exception();
    }
}

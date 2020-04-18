package com.pragyakallanagoudar.varanus.model.log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class TaskLog
{
    public TaskType type;
    public long completedTime;
    // public final String LOG_TAG = TaskLog.class.getSimpleName();

    // BEHAVIOR
    public String description;


    // FEED
    public String foodName;
    public int foodCount;

    // EXERCISE
    public int outsideTime;


    public TaskLog(TaskType type, long completedTime, /*String completedBy,*/
                   String description, String foodName, int foodCount, int outsideTime)
    {
        this.type = type;
        this.description = description;
        //this.completedBy = completedBy;
        this.completedTime = completedTime;
        this.foodName = foodName;
        this.foodCount = foodCount;
        this.outsideTime = outsideTime;
    }

    public int getOutsideTime() { return outsideTime; }

    public void setOutsideTime(int outsideTime) { this.outsideTime = outsideTime; }

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

    public void setFoodCount(int foodCount) //throws Exception
    {
        this.foodCount = foodCount;
        /**
        Log.e(LOG_TAG, "This type of TaskLog will not accept food count.");
        throw new Exception();
         */
    }

    public void setFoodName(String foodName) //throws Exception
    {
        this.foodName = foodName;
        /**
        Log.e(LOG_TAG, "This type of TaskLog will not accept food name.");
        throw new Exception();
         */
    }
}

package com.pragyakallanagoudar.varanus.model.log;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class TaskLog
{
    public long completedTime; // the time at which this task was completed
    public String user; // the user who is completing the task

    public TaskLog (long completedTime) { this.completedTime = completedTime; }

    public TaskLog () {}

    public long getCompletedTime()
    {
        return completedTime;
    }

    public void setCompletedTime(long completedTime)
    {
        this.completedTime = completedTime;
    }

    public String getUser()
    {
        if (user != null)
            return user;
        else
            return "Unidentified User";
    }

    public void setUser(String user) { this.user = user; }

    public void setFoodCount(int foodCount) throws Exception
    {
        throw new Exception();
    }

    public void setFoodName(String foodName) throws Exception
    {
        throw new Exception();
    }

    public void setOutsideTime(int outsideTime) throws Exception
    {
        throw new Exception();
    }

    public void setBehaviorText (String behaviorText) throws Exception
    {
        throw new Exception();
    }

    public void setTask (TaskType task) throws Exception
    {
        throw new Exception();
    }

    public void setCleanLevel (int cleanLevel) throws Exception
    {
        throw new Exception();
    }

    public void setWhatCleaned (String whatCleaned) throws Exception
    {
        throw new Exception();
    }

    @Override
    public String toString()
    {
        return "TaskLog";
    }

}

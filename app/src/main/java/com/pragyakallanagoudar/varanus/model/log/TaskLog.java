package com.pragyakallanagoudar.varanus.model.log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class TaskLog
{
    public long completedTime;

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

}

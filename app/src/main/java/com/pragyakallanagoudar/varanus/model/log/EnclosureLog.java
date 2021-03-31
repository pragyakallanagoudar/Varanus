package com.pragyakallanagoudar.varanus.model.log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class EnclosureLog extends TaskLog
{
    public TaskType task; // the type of task completed (CLEAN or ENRICH)
    public int cleanLevel; // 0: not cleaned; 1: cleaned; 2: deep cleaned
    public String whatCleaned; // enclosure, hide, or feces

    public EnclosureLog(long completedTime, TaskType task, int cleanLevel, String whatCleaned)
    {
        super(completedTime);
        this.task = task;
        this.cleanLevel = cleanLevel;
        this.whatCleaned = whatCleaned;
    }

    public EnclosureLog () {}

    @Override
    public void setTask (TaskType task)
    {
        this.task = task;
    }

    public TaskType getTask ()
    {
        return task;
    }

    public int getCleanLevel() { return cleanLevel; }

    public void setCleanLevel(int cleanLevel) { this.cleanLevel = cleanLevel; }

    public String getWhatCleaned() { return whatCleaned;}

    public void setWhatCleaned(String whatCleaned) { this.whatCleaned = whatCleaned; }

    @Override
    public String toString()
    {
        if (task == TaskType.CLEAN)
        {
            if (cleanLevel == 0) return "Enclosure not cleaned";
            if (cleanLevel == 1) return "Enclosure cleaned";
            else return "Enclosure deep cleaned";
        }
        else
            return "Enclosure enriched";
    }
}

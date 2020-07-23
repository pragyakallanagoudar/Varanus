package com.pragyakallanagoudar.varanus.model.log;

public class TextLog extends TaskLog
{
    public String behaviorText; // the description of the abnormal behavior being observed

    public TextLog(long completedTime, String behaviorText)
    {
        super(completedTime);
        this.behaviorText = behaviorText;
    }

    public TextLog() {}

    @Override
    public void setBehaviorText(String behaviorText)
    {
        this.behaviorText = behaviorText;
    }

    public String getBehaviorText () { return behaviorText; }

    @Override
    public String toString()
    {
        return behaviorText;
    }


}

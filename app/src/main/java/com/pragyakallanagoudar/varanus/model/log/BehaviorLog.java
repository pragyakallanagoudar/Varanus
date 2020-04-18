package com.pragyakallanagoudar.varanus.model.log;


public class BehaviorLog extends TaskLog
{
    public String behaviorText;
    public BehaviorLog (long completedTime, String behaviorText)
    {
        super(completedTime);
        this.behaviorText = behaviorText;
    }

    public void setBehaviorText(String behaviorText)
    {
        this.behaviorText = behaviorText;
    }
}

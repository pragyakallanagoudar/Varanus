package com.pragyakallanagoudar.varanus.model.log;

public class GeneralLog extends TaskLog
{
    public String generalText;

    public GeneralLog (long completedTime, String generalText)
    {
        super(completedTime);
        this.generalText = generalText;
    }

    public GeneralLog () {}

    public void setGeneralText(String behaviorText)
    {
        this.generalText = generalText;
    }

    public String setBehaviorText () { return generalText; }
}

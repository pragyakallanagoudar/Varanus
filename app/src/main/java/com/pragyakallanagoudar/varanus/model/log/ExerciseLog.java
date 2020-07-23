package com.pragyakallanagoudar.varanus.model.log;

public class ExerciseLog extends TaskLog
{
    public int outsideTime; // the time spent outside

    public ExerciseLog(long completedTime, int outsideTime)
    {
        super(completedTime);
        this.outsideTime = outsideTime;
    }

    public ExerciseLog () {}

    @Override
    public void setOutsideTime(int outsideTime)
    {
        this.outsideTime = outsideTime;
    }

    public int getOutsideTime() { return outsideTime; }

    @Override
    public String toString() {
        return outsideTime + " minutes spent outside";
    }
}

package com.pragyakallanagoudar.varanus.model.log;


import android.os.Parcel;

public class ExerciseLog extends TaskLog
{
    public int outsideTime;

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

    /**
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(completedTime);
        parcel.writeString(user);
        parcel.writeInt(outsideTime);
    }
    */
}

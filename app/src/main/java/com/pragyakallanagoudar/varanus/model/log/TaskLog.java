package com.pragyakallanagoudar.varanus.model.log;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pragyakallanagoudar.varanus.model.TaskType;

public class TaskLog // implements Parcelable
{
    public long completedTime;
    public String user;

    public TaskLog (long completedTime) { this.completedTime = completedTime; }

    public TaskLog () {}

    /**
    protected TaskLog(Parcel in) {
        Log.e("TaskLog", "tasklog parcel constructor");
        completedTime = in.readLong();
        user = in.readString();
    }


    public static final Creator<TaskLog> CREATOR = new Creator<TaskLog>() {
        @Override
        public TaskLog createFromParcel(Parcel in) {
            Log.e("TaskLog", "tasklog createFromParcel()");
            return new TaskLog(in);
        }

        @Override
        public TaskLog[] newArray(int size) {
            Log.e("TaskLog", "tasklog newArray()");
            return new TaskLog[size];
        }
    };
     */

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

    /**
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.e("TaskLog", "tasklog writeToParcel()");
        parcel.writeLong(completedTime);
        parcel.writeString(user);
    }
*/
    @Override
    public String toString()
    {
        return "TaskLog";
    }

}

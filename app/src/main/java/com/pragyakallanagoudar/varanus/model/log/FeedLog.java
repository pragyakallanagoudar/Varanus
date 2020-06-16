package com.pragyakallanagoudar.varanus.model.log;


import android.os.Parcel;
import android.util.Log;

public class FeedLog extends TaskLog
{
    public String foodName;
    public int foodCount;

    public FeedLog(long completedTime)
    {
        super(completedTime);

    }

    public FeedLog(long completedTime, String foodName, int foodCount)
    {
        super(completedTime);
        this.foodName = foodName;
        this.foodCount = foodCount;
    }

    /**
    protected FeedLog(Parcel in) {
        Log.e("FeedLog", "feedlog parcel constructor");
        completedTime = in.readLong();
        user = in.readString();
        foodName = in.readString();
        foodCount = in.readInt();
    }

    public static final Creator<FeedLog> CREATOR = new Creator<FeedLog>() {
        @Override
        public FeedLog createFromParcel(Parcel in) {
            Log.e("FeedLog", "feedlog createFromParcel()");
            return new FeedLog(in);
        }

        @Override
        public FeedLog[] newArray(int size) {
            Log.e("FeedLog", "feedlog newArray()");
            return new FeedLog[size];
        }
    };
    */
    public FeedLog () {}


    public int getFoodCount() { return foodCount; }

    public String getFoodName() { return foodName; }

    @Override
    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    @Override
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    @Override
    public String toString() {
        return foodCount + " " + foodName + " eaten";
    }

    /**
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.e("FeedLog", "feedlog writeToParcel()");
        parcel.writeLong(completedTime);
        parcel.writeString(user);
        parcel.writeString(foodName);
        parcel.writeInt(foodCount);
    }
    */
}

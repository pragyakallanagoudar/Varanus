package com.pragyakallanagoudar.varanus.model.log;


import android.os.Parcel;

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
        return "FeedLog{" +
                "foodName='" + foodName + '\'' +
                ", foodCount=" + foodCount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(completedTime);
        parcel.writeString(user);
        parcel.writeString(foodName);
        parcel.writeInt(foodCount);
    }
}

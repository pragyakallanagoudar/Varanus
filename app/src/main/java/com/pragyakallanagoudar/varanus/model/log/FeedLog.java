package com.pragyakallanagoudar.varanus.model.log;


import android.os.Parcel;
import android.util.Log;

public class FeedLog extends TaskLog
{
    public String foodName; // the type of food that was fed to the animal
    public int foodCount; // the amount of food fed to the animal

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
        return foodCount + " " + foodName + " eaten";
    }
}

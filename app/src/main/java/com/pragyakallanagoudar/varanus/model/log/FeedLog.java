package com.pragyakallanagoudar.varanus.model.log;


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
        foodName = this.foodName;
        foodCount = this.foodCount;
    }

    public int getFoodCount() {
        return foodCount;
    }

    @Override
    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    public String getFoodName() {
        return foodName;
    }

    @Override
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}

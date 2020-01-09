package com.pragyakallanagoudar.varanus;

import java.util.HashMap;

public class Meal
{
    public HashMap<String, Integer> meal;

    public Meal()
    {
        meal = new HashMap<String, Integer>();
    }

    public void add (Food food, Integer amount)
    {
        meal.put(food.toString(), amount);
    }

    public void add (String food, Integer amount)
    {
        meal.put(food.toUpperCase(), amount);
    }

}

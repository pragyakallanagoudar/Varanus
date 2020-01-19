package com.pragyakallanagoudar.varanus.arbitrary;

import com.pragyakallanagoudar.varanus.model.Meal;

import java.util.Calendar;
import java.util.HashMap;

public class BehaviorLog
{
    // Hashmap of Date, BehaviorDescription. Dates are converted to String format.
    public HashMap<String, String> log;

    public BehaviorLog()
    {
        log = new HashMap<String, String>();
    }

    /** Add an entry to the abnormal behavior log with a description of the behavior. */
    public void makeEntry(String description)
    {
        log.put(Calendar.getInstance().getTime().toString(), description);
    }

    public static class Diet
    {
        public HashMap<String, Meal> mealLog;

        public Diet ()
        {
            mealLog = new HashMap<String, Meal>();
        }

        public void addMeal(Meal meal)
        {
            mealLog.put(Calendar.getInstance().getTime().toString(), meal);
        }
    }

    public static class OutsideLog
    {
        public HashMap<String, Integer> log;

        public OutsideLog ()
        {
            log = new HashMap<String, Integer>();
        }

        /** Makes an entry with current date and given # of minutes. */
        public void makeEntry(Integer time)
        {
            log.put(Calendar.getInstance().getTime().toString(), time);
        }

    }
}
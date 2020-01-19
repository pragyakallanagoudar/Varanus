package com.pragyakallanagoudar.varanus.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Animal
{
    /** GET LOG TAG */
    private static final String LOG_TAG = Animal.class.getSimpleName();
    public String name; // name of the animal
    public String species; // species of the animal
    public HashMap<String, Meal> diet; // HashMap: diet of the animal. Date:Meal format
    public HashMap<String, Integer> outsideLog; // HashMap: log of outside time. Date:# Minutes format
    public HashMap<String, String> behaviorLog; // HashMap: behavior, especially abnormal. Date:description format

    public Animal ()
    {
        name = "Unspecified";
        species = "Unspecified";
        diet = new HashMap<String, Meal>();
        outsideLog = new HashMap<String, Integer>();
        behaviorLog = new HashMap<String, String>();
    }

    public Animal (String nameIn, String speciesIn)
    {
        name = nameIn;
        species = speciesIn;
        diet = new HashMap<String, Meal>();
        outsideLog = new HashMap<String, Integer>();
        behaviorLog = new HashMap<String, String>();
    }

    public void addMeal(Meal meal)
    {

        diet.put(getCurrentDate(), meal);
    }

    public void addMeal(String date, Meal meal)
    {
        diet.put(date, meal);
    }

    public void logOutsideTime(Integer time)
    {
        outsideLog.put(getCurrentDate(), time);
    }

    public void logOutsideTime(String date, Integer time)
    {
        outsideLog.put(date, time);
    }

    public void logBehavior(String description)
    {
        behaviorLog.put(getCurrentDate(), description);
    }

    public void logBehavior(String date, String description)
    {
        behaviorLog.put(date, description);
    }

    /**
     * Calendar.getInstance().getTime() returns the date in the format:
     * Mon Dec 30 19:26:21 PST 2019
     * getCurrentDate() extracts the time + timezone (unneeded) from this String
     * using regular expressions to get
     * Mon Dec 30 2019
     * The time has the regex: "\s\d\d:\d\d:\d\d\sPST"
     */
    public String getCurrentDate()
    {
        String date = Calendar.getInstance().getTime().toString();
        Pattern pattern = Pattern.compile("\\s\\d\\d:\\d\\d:\\d\\d\\sPST");
        Matcher matcher = pattern.matcher(date);
        if (matcher.find())
        {
            String match = matcher.group();
            date = date.substring(0, date.indexOf(match))
                    + date.substring(date.indexOf(match) + match.length());
        }
        return date;
    }
}
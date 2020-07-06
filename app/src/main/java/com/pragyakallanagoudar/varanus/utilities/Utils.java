package com.pragyakallanagoudar.varanus.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The utilities class has widely used methods for use in all other classes.
 */

public class Utils {

    public static boolean adminControls; // Whether or not the admin controls have been enabled.

    /** Retrieves only the first name of the user */
    public static String getFirstName (String user)
    {
        if (user.contains(" "))
            return user.substring(0, user.indexOf(" "));
        return user;
    }

    /** Retrieves a simple version of the date */
    public static String getDate (String verbose)
    {
        Pattern pattern = Pattern.compile("\\w{3} \\w+ \\d{2}");
        Matcher matcher = pattern.matcher(verbose);
        if (matcher.find())
        {
            return matcher.group();
        }
        return verbose;
    }

}

package com.sellics.analytics.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static final String REG_EXP = "^[a-zA-Z0-9 ]*$";

    public static boolean isStringContainsSpecialChar(final String inputString) {

        Pattern pattern = Pattern.compile(REG_EXP);
        Matcher matcher = pattern.matcher(inputString);

        return !matcher.find();
    }
}

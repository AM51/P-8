package com.google.android.gms.fit.samples.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by archit.m on 03/08/16.
 */
public class Utils {

    public static final String EXERCISE_NAME = "exercise_name";
    public static final String WORKOUT_LOG = "workout_log";
    public static final String WORKOUT_DAY = "workout_day";
    public static final String WORKOUT_MONTH = "workout_month";
    public static final String WORKOUT_YEAR = "workout_year";
    public static final String WORKOUT_DATE = "workout_date";
    public static final String MUSCLE_CHEST = "chest";
    public static final String MUSCLE_BICEPS = "biceps";
    public static final String MUSCLE_BACK = "back";
    public static final String MUSCLE_SHOULDERS = "shoulders";
    public static final String MUSCLE_TRICEPS = "triceps";
    public static final String MUSCLE_LEGS = "legs";
    public static final String CURRENT_DATE_SELECTED = "current_date";

    public static long getTimeStampForStartOfToday(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        long currentTimeStamp = (new Date()).getTime();

        long timeStampForStartOfDay  = currentTimeStamp - ((hour*3600000)+(minute*60000)+(second*1000)+millis);

        return timeStampForStartOfDay;
    }

    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }


}

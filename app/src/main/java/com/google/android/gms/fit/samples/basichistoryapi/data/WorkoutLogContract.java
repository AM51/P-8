package com.google.android.gms.fit.samples.basichistoryapi.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogContract {

    public  final static String CONTENT_AUTHORITY = "com.google.android.gms.fit.samples.logs";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public final static String PATH_WORKOUT_LOGS =  "logs";

    public final static class WLog {
        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT_LOGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_LOGS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_LOGS;

        public final static String TABLE_NAME = "workout_logs";

        public final static String COLUMN_LOG_ID = "workout_log_id";

        public final static String COLUMN_LOG_EXERCISE = "workout_log_exercise";

        public final static String COLUMN_LOG_WEIGHT = "workout_log_weight";

        public final static String COLUMN_LOG_REPETITIONS = "workout_reps";

        public final static String COLUMN_TIMESTAMP = "workout_log_timestamp";


        public static Uri buildUri(){
            return CONTENT_URI;
            //return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}

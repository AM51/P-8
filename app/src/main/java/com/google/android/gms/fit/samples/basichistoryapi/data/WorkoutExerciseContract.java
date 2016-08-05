package com.google.android.gms.fit.samples.basichistoryapi.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by archit.m on 05/08/16.
 */
public class WorkoutExerciseContract {
    public  final static String CONTENT_AUTHORITY = "com.google.android.gms.fit.samples";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public final static String PATH_WORKOUT_EXERCISE =  "exercise";

    public final static class WorkoutExercise {
        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT_EXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_EXERCISE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_EXERCISE;

        public final static String TABLE_NAME = "exercises";

        public final static String COLUMN_EXERCISE_ID = "exercise_id";

        public final static String COLUMN_EXERCISE_CATEGORY = "exercise_category";

        public final static String COLUMN_EXERCISE_DISPLAY_NAME = "exercise_display_name";

        public final static String COLUMN_EXERCISE_GOOGLE_FIT_VALUE = "exercise_google_fit_value";

        public static Uri buildUri(Long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}

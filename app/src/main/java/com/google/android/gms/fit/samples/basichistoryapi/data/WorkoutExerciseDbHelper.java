package com.google.android.gms.fit.samples.basichistoryapi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by archit.m on 05/08/16.
 */
public class WorkoutExerciseDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "exercise.db";

    public WorkoutExerciseDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXERCISES_TABLE = "CREATE TABLE "+ WorkoutExerciseContract.WorkoutExercise.TABLE_NAME + " (  "+
//                WorkoutExerciseContract.WorkoutExercise._ID +" INTEGER PRIMARY KEY ," +
                WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_ID + " VARCHAR(255) PRIMARY KEY, " +
                WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY+" VARCHAR(255), "+
                WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME+" VARCHAR(255), "+
                WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_GOOGLE_FIT_VALUE+" VARCHAR(255) "+
                " );";

        db.execSQL(CREATE_EXERCISES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ WorkoutExerciseContract.WorkoutExercise.TABLE_NAME);
        onCreate(db);
    }
}

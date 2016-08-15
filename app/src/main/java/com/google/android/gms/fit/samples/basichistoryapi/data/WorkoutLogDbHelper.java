package com.google.android.gms.fit.samples.basichistoryapi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "workout.log.db";

    public WorkoutLogDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGS_TABLE = "CREATE TABLE "+ WorkoutLogContract.WLog.TABLE_NAME + " (  "+
//                WorkoutExerciseContract.WorkoutExercise._ID +" INTEGER PRIMARY KEY ," +
                WorkoutLogContract.WLog.COLUMN_LOG_ID + " VARCHAR(255) PRIMARY KEY, " +
                WorkoutLogContract.WLog.COLUMN_LOG_EXERCISE+" VARCHAR(255), "+
                WorkoutLogContract.WLog.COLUMN_LOG_WEIGHT+" VARCHAR(255), "+
                WorkoutLogContract.WLog.COLUMN_TIMESTAMP+" BIGINT, "+
                WorkoutLogContract.WLog.COLUMN_LOG_REPETITIONS+" VARCHAR(255) "+
                " );";

        db.execSQL(CREATE_LOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ WorkoutExerciseContract.WorkoutExercise.TABLE_NAME);
        onCreate(db);
    }
}

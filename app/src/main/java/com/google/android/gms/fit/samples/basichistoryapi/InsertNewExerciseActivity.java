package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;

import java.util.UUID;

public class InsertNewExerciseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        insertIntoDb();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void insertIntoDb() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_ID, UUID.randomUUID().toString());
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY,"Biceps");
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME,"TestCurl");
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_GOOGLE_FIT_VALUE,"test_curl");
        Uri uri = getApplicationContext().getContentResolver().insert(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, contentValues);
        Cursor cursor = getApplicationContext().getContentResolver().query(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        int ind = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_ID);
        int indExerciseName = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME);
        int indExerciseGFName = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_GOOGLE_FIT_VALUE);

        String string = cursor.getString(ind);
        String exerciseName = cursor.getString(indExerciseName);
        String exerciseGFValue = cursor.getString(indExerciseGFName);
        Log.e("archit","Workout exercise Id :: "+string+ " "+exerciseName+" "+exerciseGFValue);
    }

}

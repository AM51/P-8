package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;
import com.google.android.gms.fit.samples.basichistoryapi.widget.WidgetDataProvider;
import com.google.android.gms.fit.samples.basichistoryapi.widget.WorkoutLogsWidget;
import com.google.android.gms.fit.samples.utils.Utils;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddExerciseLogActivity extends AppCompatActivity {

    @BindView(R.id.saveExerciseLog) Button saveButton;
    @BindView(R.id.weight_entry) EditText weightEntry;
    @BindView(R.id.reps_entry) EditText repsEntry;

    private int reps;
    private float weight;
    private String exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExerciseLog);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        exercise = intent.getStringExtra(Utils.EXERCISE_NAME);
        Log.e("archit",exercise+ " wohoo");
        ButterKnife.bind(this);
//        saveButton = (Button)findViewById(R.id.saveExerciseLog);
//        weightEntry = (EditText)findViewById(R.id.weight_entry);
//        repsEntry = (EditText)findViewById(R.id.reps_entry);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable weightEntryText = weightEntry.getText();
                Editable repsEntryText = repsEntry.getText();

                if( (weightEntryText == null || Utils.isEmptyString(weightEntryText.toString())) ||
                        (repsEntryText == null || Utils.isEmptyString(repsEntryText.toString())) ){
                    Toast.makeText(getApplicationContext(), "Please fill both reps and weight entries", Toast.LENGTH_LONG).show();
                } else {

                    float weightLog = Float.parseFloat(weightEntry.getText().toString());
                    int repsLog = Integer.parseInt(repsEntry.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    WorkoutLog workoutLog = new WorkoutLog(exercise, repsLog, weightLog); //todo add exercise name , weight , reps
                    intent.putExtra(Utils.WORKOUT_LOG, workoutLog);
                    startActivity(intent);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_ID, UUID.randomUUID().toString());
                    contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_EXERCISE, exercise);
                    contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_WEIGHT, String.valueOf(weightLog));
                    contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_REPETITIONS, String.valueOf(repsLog));
                    contentValues.put(WorkoutLogContract.WLog.COLUMN_TIMESTAMP, new Date().getTime());
                    getApplicationContext().getContentResolver().insert(WorkoutLogContract.WLog.CONTENT_URI, contentValues);
                    //Intent i = new Intent(getApplicationContext(), WorkoutLogsWidget.class);
                    Intent i = new Intent(WorkoutLogsWidget.DATABASE_CHANGED);
                    //i.setAction(WorkoutLogsWidget.DATABASE_CHANGED);
                    getApplicationContext().sendBroadcast(i);
                    Log.e("archit", "Exercise log inserted into db");

                }
            }
        });
    }

}

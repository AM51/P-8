package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.utils.Utils;

public class AddExerciseLogActivity extends AppCompatActivity {

    Button saveButton;
    EditText weightEntry;
    EditText repsEntry;

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
        saveButton = (Button)findViewById(R.id.saveExerciseLog);
        weightEntry = (EditText)findViewById(R.id.weight_entry);
        repsEntry = (EditText)findViewById(R.id.reps_entry);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float weightLog = Float.parseFloat(weightEntry.getText().toString());
                int repsLog = Integer.parseInt(repsEntry.getText().toString());
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                WorkoutLog workoutLog = new WorkoutLog(exercise,repsLog,weightLog); //todo add exercise name , weight , reps
                intent.putExtra(Utils.WORKOUT_LOG,workoutLog);
                startActivity(intent);
            }
        });
    }

}

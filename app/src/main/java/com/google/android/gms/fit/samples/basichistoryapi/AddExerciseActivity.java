package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddExerciseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private static List<String> muscles;
    static {
        muscles = new ArrayList<>();
        muscles.add(Utils.MUSCLE_CHEST);
        muscles.add(Utils.MUSCLE_BICEPS);
        muscles.add(Utils.MUSCLE_BACK);
        muscles.add(Utils.MUSCLE_SHOULDERS);
        muscles.add(Utils.MUSCLE_SHOULDERS);
        muscles.add(Utils.MUSCLE_TRICEPS);
        muscles.add(Utils.MUSCLE_LEGS);
    }

    String muscleCategorySelected;

    @BindView(R.id.musclesCategories) Spinner muscleCategories;
    @BindView(R.id.addExercise) Button addExerciseButton;
    @BindView(R.id.exerciseName) EditText exerciseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Exercise");
        setSupportActionBar(toolbar);


        ButterKnife.bind(this);
//        muscleCategories = (Spinner)findViewById(R.id.musclesCategories);
//        addExerciseButton = (Button)findViewById(R.id.addExercise);
//        exerciseName = (EditText)findViewById(R.id.exerciseName);
        addExerciseButton.setOnClickListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,muscles);
        muscleCategories.setAdapter(arrayAdapter);
        muscleCategories.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        muscleCategorySelected = item;
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        String exerciseNameText = exerciseName.getText().toString();
        Toast.makeText(getApplicationContext(), "Selected: " + exerciseNameText+" "+muscleCategorySelected, Toast.LENGTH_LONG).show();
        insertIntoDb(muscleCategorySelected,exerciseNameText);

    }

    private void insertIntoDb(String category,String exerciseName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_ID, UUID.randomUUID().toString());
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY,category);
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME,exerciseName);
        contentValues.put(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_GOOGLE_FIT_VALUE,exerciseName);
        Uri uri = getApplicationContext().getContentResolver().insert(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, contentValues);
        Cursor cursor = getApplicationContext().getContentResolver().query(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        int ind = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_ID);
        int indExerciseName = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME);
        int indExerciseGFName = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_GOOGLE_FIT_VALUE);

        String string = cursor.getString(ind);
        String exercise = cursor.getString(indExerciseName);
        String exerciseGFValue = cursor.getString(indExerciseGFName);
        Log.e("archit","Workout exercise Id :: "+string+ " "+exercise+" "+exerciseGFValue);
    }
}

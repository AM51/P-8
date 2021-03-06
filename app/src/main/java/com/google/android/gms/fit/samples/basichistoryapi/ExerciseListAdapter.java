package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by archit.m on 02/08/16.
 */
public class ExerciseListAdapter extends BaseAdapter{

    private static final String selection = WorkoutExerciseContract.WorkoutExercise.TABLE_NAME+"."+ WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY+"= ?";
    ArrayList<String> strings =new ArrayList<>();
    Context context;

    public ExerciseListAdapter(Context context,String selectedMuscle) {
        this.context = context;
        //buildExerciseListForMuscle(selectedMuscle);
        //strings.add("Curl");
        //strings.add("Press");
    }

    @Override
    public int getCount() {
        //Log.v("archit",""+strings.size());
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView = new TextView(context);
        textView.setText(strings.get(position));
        textView.setTextColor(Color.BLACK);
        textView.setHeight(100);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        //Log.v("archit",""+position+" "+strings.get(position));
        return textView;
    }

    private void buildExerciseListForMuscle(String muscle){
        Log.e("test","Fetching exercise from db");
        Cursor cursor = context.getContentResolver().query(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, null, selection, new String[]{muscle}, null);
        //Cursor cursor = context.getContentResolver().query(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, null, null, null, null);
        int ind = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME);
        int ind1 = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY);

        while (cursor.moveToNext()){
            String exerciseName = cursor.getString(ind);
            String category = cursor.getString(ind1);
            strings.add(exerciseName);
            Log.e("test","List element from db: "+exerciseName+" "+category);
        }

        List<String> inbuiltExercises = getInbuiltExercises(muscle);
        strings.addAll(inbuiltExercises);

    }

    public void setStrings(ArrayList<String> strings, String selectedMuscle) {
        List<String> inbuiltExercises = getInbuiltExercises(selectedMuscle);
        strings.addAll(inbuiltExercises);
        this.strings = strings;
    }

    public  List<String> getInbuiltExercises(String muscle){

        switch (muscle){
            case Utils.MUSCLE_CHEST:
                return Arrays.asList("Chest1","Chest2");
            case Utils.MUSCLE_BICEPS:
                return Arrays.asList("Biceps1","Biceps2");
            case Utils.MUSCLE_BACK:
                return Arrays.asList("Back1","Back2");
            case Utils.MUSCLE_LEGS:
                return Arrays.asList("Legs1","Legs2");
            case Utils.MUSCLE_SHOULDERS:
                return Arrays.asList("Shoulders1","Shoulders2");
            case Utils.MUSCLE_TRICEPS:
                return Arrays.asList("Triceps1","Triceps2");
            default:
                return Arrays.asList("Test1","Test2");

        }

    }
}
